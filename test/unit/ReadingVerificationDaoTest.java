package test.unit;
import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import dao.DAO;
import dao.ReadingVerificationDAO;
import model.ReadingVerification;

public class ReadingVerificationDaoTest {
	ReadingVerificationDAO rvd = new ReadingVerificationDAO();
	
	/* ------------------------ addVerification() ------------------------ */
	
	@Test
	public void testAddVerificationStandard() throws Exception {//reading 2 exists, no verification yet
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			ReadingVerification v = new ReadingVerification("Lech 15 kWh", "Kiem tra tai phong");
			Assert.assertTrue(rvd.addVerification(2, v));
			Assert.assertTrue(v.getId() > 0);
			
			//test the new inserted row
			ReadingVerification saved = rvd.getVerificationByReading(2);
			Assert.assertNotNull(saved);
			Assert.assertEquals(v.getId(), saved.getId());
			Assert.assertEquals("Lech 15 kWh", saved.getReason());
			Assert.assertEquals("Kiem tra tai phong", saved.getMethod());
			Assert.assertEquals(ReadingVerification.STATUS_OPEN, saved.getStatus());
			Assert.assertNotNull(saved.getRequestedAt());
			Assert.assertNull(saved.getClosedAt());
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	@Test
	public void testAddVerificationException() throws Exception {//reading does not exist
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			ReadingVerification v = new ReadingVerification("Lech 15 kWh", "Kiem tra tai phong");
			Assert.assertFalse(rvd.addVerification(999, v));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	/* ------------------------ getVerificationByReading() ------------------------ */
	
	@Test
	public void testGetVerificationByReadingStandard(){//reading 8 has an open verification (and an older closed one)
		ReadingVerification v = rvd.getVerificationByReading(8);
		Assert.assertNotNull(v);
		Assert.assertEquals(1, v.getId());
		Assert.assertEquals("Lech lon", v.getReason());
		Assert.assertEquals("Kiem tra tai phong", v.getMethod());
		Assert.assertEquals(ReadingVerification.STATUS_OPEN, v.getStatus());
		Assert.assertNull(v.getResult());
	}
	
	@Test
	public void testGetVerificationByReadingException1(){//reading 4 has only a closed verification
		Assert.assertNull(rvd.getVerificationByReading(4));
	}
	
	@Test
	public void testGetVerificationByReadingException2(){//reading 1 has no verification
		Assert.assertNull(rvd.getVerificationByReading(1));
	}
	
	/* ------------------------ resolveVerification() ------------------------ */
	
	@Test
	public void testResolveVerificationStandard() throws Exception {//verification 1 is open
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			ReadingVerification v = rvd.getVerificationByReading(8);
			Assert.assertNotNull(v);
			v.setResult("Nguoi thue ghi nham chi so");
			Assert.assertTrue(rvd.resolveVerification(v));
			
			//the verification is closed: there is no open verification any more
			Assert.assertNull(rvd.getVerificationByReading(8));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	@Test
	public void testResolveVerificationException() throws Exception {//verification does not exist
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			ReadingVerification v = new ReadingVerification();
			v.setId(999);
			v.setResult("xxx");
			Assert.assertFalse(rvd.resolveVerification(v));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
}
