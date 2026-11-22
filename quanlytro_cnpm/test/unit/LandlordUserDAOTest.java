package unit;

import dao.UserDAO;
import entity.Contract;
import entity.User;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LandlordUserDAOTest {
    private User login(UserDAO dao, String username, String password) {
        User input = new User();
        input.setUsername(username);
        input.setPasswordHash(password);
        return dao.checkLogin(input);
    }

    @Test
    public void testLandlordLoginAndAllContracts() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            User user = login(new UserDAO(con), "landlord_test", "123456");
            Assert.assertNotNull(user);
            Assert.assertEquals("LANDLORD", user.getRole());
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tblContract")) {
                Assert.assertTrue(rs.next());
                Assert.assertEquals(rs.getInt(1), user.getContracts().size());
            }
            for (Contract c : user.getContracts()) Assert.assertTrue(c.getId() > 0);
        }
    }

    @Test
    public void testTenantContractsStillScoped() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            User user = login(new UserDAO(con), "tenant_test", "123456");
            Assert.assertNotNull(user);
            Assert.assertEquals("TENANT", user.getRole());
            Assert.assertEquals(1, user.getContracts().size());
            Assert.assertEquals("TEST-HD-001", user.getContracts().get(0).getCode());
        }
    }

    @Test
    public void testInvalidLandlordPassword() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            Assert.assertNull(login(new UserDAO(con), "landlord_test", "wrong"));
        }
    }
}
