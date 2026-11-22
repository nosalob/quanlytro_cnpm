package test.unit;
import java.sql.Connection;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import dao.DAO;
import dao.MeterReadingDAO;
import model.MeterReading;

public class MeterReadingDaoTest {
	MeterReadingDAO mrd = new MeterReadingDAO();
	
	/**
	 * check that the list contains exactly the readings whose ids are @ids, in that order
	 */
	private void assertIds(ArrayList<MeterReading> list, int... ids) {
		Assert.assertNotNull(list);
		Assert.assertEquals(ids.length, list.size());
		for(int i=0; i<ids.length; i++) {
			Assert.assertEquals(ids[i], list.get(i).getId());
			Assert.assertNotNull(list.get(i).getRoom());
		}
	}
	
	/* ------------------------ searchMeterReading() ------------------------ */
	
	@Test
	public void testSearchMeterReadingStandard1(){//building 1, 11/2026, PENDING: 3 readings
		ArrayList<MeterReading> list = mrd.searchMeterReading(1, 11, 2026, MeterReading.STATUS_PENDING);
		assertIds(list, 1, 2, 3);
		//ordered by room code, then by type
		Assert.assertEquals("P101", list.get(0).getRoom().getCode());
		Assert.assertEquals("ELECTRIC", list.get(0).getType());
		Assert.assertEquals("P101", list.get(1).getRoom().getCode());
		Assert.assertEquals("WATER", list.get(1).getType());
		Assert.assertEquals("P102", list.get(2).getRoom().getCode());
		Assert.assertEquals("ELECTRIC", list.get(2).getType());
		Assert.assertEquals(1240, list.get(0).getPreviousValue(), 0.0001);
		Assert.assertEquals(1318, list.get(0).getCurrentValue(), 0.0001);
		Assert.assertEquals(MeterReading.STATUS_PENDING, list.get(0).getStatus());
	}
	
	@Test
	public void testSearchMeterReadingStandard2(){//building 1, 11/2026, APPROVED: only reading 4
		assertIds(mrd.searchMeterReading(1, 11, 2026, MeterReading.STATUS_APPROVED), 4);
	}
	
	@Test
	public void testSearchMeterReadingStandard3(){//building 1, 11/2026, VERIFYING: only reading 8
		assertIds(mrd.searchMeterReading(1, 11, 2026, MeterReading.STATUS_VERIFYING), 8);
	}
	
	@Test
	public void testSearchMeterReadingStandard4(){//building 1, 10/2026, PENDING: only reading 6
		assertIds(mrd.searchMeterReading(1, 10, 2026, MeterReading.STATUS_PENDING), 6);
	}
	
	@Test
	public void testSearchMeterReadingStandard5(){//building 2, 11/2026, PENDING: only reading 7 (other building is excluded)
		ArrayList<MeterReading> list = mrd.searchMeterReading(2, 11, 2026, MeterReading.STATUS_PENDING);
		assertIds(list, 7);
		Assert.assertEquals("P201", list.get(0).getRoom().getCode());
	}
	
	@Test
	public void testSearchMeterReadingException1(){//no reading in the month 12/2026
		assertIds(mrd.searchMeterReading(1, 12, 2026, MeterReading.STATUS_PENDING));
	}
	
	@Test
	public void testSearchMeterReadingException2(){//the building does not exist
		assertIds(mrd.searchMeterReading(999, 11, 2026, MeterReading.STATUS_PENDING));
	}
	
	/* ------------------------ getReadingDetail() ------------------------ */
	
	@Test
	public void testGetReadingDetailStandard1(){//reading with an e-invoice, not reviewed yet
		MeterReading mr = mrd.getReadingDetail(1);
		Assert.assertNotNull(mr);
		Assert.assertEquals(1, mr.getId());
		Assert.assertEquals(11, mr.getMonth());
		Assert.assertEquals(2026, mr.getYear());
		Assert.assertEquals("ELECTRIC", mr.getType());
		Assert.assertEquals(1240, mr.getPreviousValue(), 0.0001);
		Assert.assertEquals(1318, mr.getCurrentValue(), 0.0001);
		Assert.assertEquals(78, mr.getConsumption(), 0.0001);
		Assert.assertNotNull(mr.getEInvoiceValue());
		Assert.assertEquals(1315, mr.getEInvoiceValue(), 0.0001);
		Assert.assertEquals(3, mr.calcDeviation(), 0.0001);
		Assert.assertEquals(MeterReading.STATUS_PENDING, mr.getStatus());
		Assert.assertEquals("P101", mr.getRoom().getCode());
		Assert.assertEquals(25f, mr.getRoom().getArea(), 0.0001f);
		Assert.assertEquals("Tran Thi B", mr.getSubmittedBy().getFullName());
		Assert.assertNull(mr.getReviewedBy());
		Assert.assertNull(mr.getReviewedAt());
	}
	
	@Test
	public void testGetReadingDetailStandard2(){//reading without an e-invoice
		MeterReading mr = mrd.getReadingDetail(2);
		Assert.assertNotNull(mr);
		Assert.assertEquals("WATER", mr.getType());
		Assert.assertNull(mr.getEInvoiceValue());
		Assert.assertEquals(0, mr.calcDeviation(), 0.0001);
	}
	
	@Test
	public void testGetReadingDetailStandard3(){//reading already approved, with its reviewer
		MeterReading mr = mrd.getReadingDetail(4);
		Assert.assertNotNull(mr);
		Assert.assertEquals(MeterReading.STATUS_APPROVED, mr.getStatus());
		Assert.assertNotNull(mr.getReviewedBy());
		Assert.assertEquals(1, mr.getReviewedBy().getId());
		Assert.assertEquals("Nguyen Van A", mr.getReviewedBy().getFullName());
		Assert.assertNotNull(mr.getReviewedAt());
		Assert.assertEquals("Le Van C", mr.getSubmittedBy().getFullName());
		Assert.assertEquals("P103", mr.getRoom().getCode());
	}
	
	@Test
	public void testGetReadingDetailStandard4(){//reading already rejected, with its review note
		MeterReading mr = mrd.getReadingDetail(5);
		Assert.assertNotNull(mr);
		Assert.assertEquals(MeterReading.STATUS_REJECTED, mr.getStatus());
		Assert.assertEquals("Anh mo", mr.getReviewNote());
	}
	
	@Test
	public void testGetReadingDetailException(){//the reading does not exist
		Assert.assertNull(mrd.getReadingDetail(999));
	}
	
	/* ------------------------ approve() ------------------------ */
	
	@Test
	public void testApproveStandard() throws Exception {//reading 1 exists
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertTrue(mrd.approve(1, 1));
			
			//test the new updated row
			MeterReading mr = mrd.getReadingDetail(1);
			Assert.assertEquals(MeterReading.STATUS_APPROVED, mr.getStatus());
			Assert.assertNotNull(mr.getReviewedBy());
			Assert.assertEquals(1, mr.getReviewedBy().getId());
			Assert.assertNotNull(mr.getReviewedAt());
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	@Test
	public void testApproveException() throws Exception {//reading does not exist
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertFalse(mrd.approve(999, 1));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	/* ------------------------ reject() ------------------------ */
	
	@Test
	public void testRejectStandard() throws Exception {//reading 3 exists
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertTrue(mrd.reject(3, 1, "Anh khong ro"));
			
			//test the new updated row
			MeterReading mr = mrd.getReadingDetail(3);
			Assert.assertEquals(MeterReading.STATUS_REJECTED, mr.getStatus());
			Assert.assertEquals("Anh khong ro", mr.getReviewNote());
			Assert.assertNotNull(mr.getReviewedBy());
			Assert.assertEquals(1, mr.getReviewedBy().getId());
			Assert.assertNotNull(mr.getReviewedAt());
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	@Test
	public void testRejectException() throws Exception {//reading does not exist
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertFalse(mrd.reject(999, 1, "Anh khong ro"));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	/* ------------------------ updateStatus() ------------------------ */
	
	@Test
	public void testUpdateStatusStandard() throws Exception {//reading 1 exists
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertTrue(mrd.updateStatus(1, MeterReading.STATUS_VERIFYING));
			Assert.assertEquals(MeterReading.STATUS_VERIFYING, mrd.getReadingDetail(1).getStatus());
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	@Test
	public void testUpdateStatusException() throws Exception {//reading does not exist
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertFalse(mrd.updateStatus(999, MeterReading.STATUS_VERIFYING));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	/* ------------------------ updateValue() ------------------------ */
	
	@Test
	public void testUpdateValueStandard() throws Exception {//reading 2 exists
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertTrue(mrd.updateValue(2, 92.5));
			
			MeterReading mr = mrd.getReadingDetail(2);
			Assert.assertEquals(92.5, mr.getCurrentValue(), 0.0001);
			Assert.assertEquals(7.5, mr.getConsumption(), 0.0001);
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
	
	@Test
	public void testUpdateValueException() throws Exception {//reading does not exist
		Connection con = DAO.con;
		try{
			con.setAutoCommit(false);
			Assert.assertFalse(mrd.updateValue(999, 100));
		}finally{
			con.rollback();
			con.setAutoCommit(true);
		}
	}
}
