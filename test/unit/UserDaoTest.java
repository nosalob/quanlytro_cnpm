package test.unit;
import org.junit.Assert;
import org.junit.Test;
import dao.UserDAO;
import model.User;

public class UserDaoTest {
	UserDAO ud = new UserDAO();
	
	private User makeUser(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPasswordHash(User.hash(password));
		return user;
	}
	
	@Test
	public void testCheckLoginStandard1(){//landlord, correct username and password
		User user = makeUser("chutro01", "123456");
		Assert.assertTrue(ud.checkLogin(user));
		Assert.assertEquals(1, user.getId());
		Assert.assertEquals("Nguyen Van A", user.getFullName());
		Assert.assertEquals("landlord", user.getRole());
		Assert.assertEquals("ACTIVE", user.getStatus());
	}
	
	@Test
	public void testCheckLoginStandard2(){//tenant, correct username and password
		User user = makeUser("nguoithue01", "123456");
		Assert.assertTrue(ud.checkLogin(user));
		Assert.assertEquals(2, user.getId());
		Assert.assertEquals("Tran Thi B", user.getFullName());
		Assert.assertEquals("tenant", user.getRole());
	}
	
	@Test
	public void testCheckLoginException1(){//wrong password
		User user = makeUser("chutro01", "wrongpass");
		Assert.assertFalse(ud.checkLogin(user));
		Assert.assertEquals(0, user.getId());
		Assert.assertNull(user.getFullName());
		Assert.assertNull(user.getRole());
	}
	
	@Test
	public void testCheckLoginException2(){//username does not exist
		User user = makeUser("xxxxxxxx", "123456");
		Assert.assertFalse(ud.checkLogin(user));
		Assert.assertEquals(0, user.getId());
	}
	
	@Test
	public void testCheckLoginException3(){//empty username and password
		User user = makeUser("", "");
		Assert.assertFalse(ud.checkLogin(user));
		Assert.assertEquals(0, user.getId());
	}
}
