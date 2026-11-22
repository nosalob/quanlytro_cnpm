
package unit;

import dao.UserDAO;
import entity.Contract;
import entity.User;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.util.List;

public class UserDAOTest {

    private User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        return user;
    }

    @Test
    public void testCheckLoginStandard1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            UserDAO dao = new UserDAO(con);

            User user = dao.checkLogin(
                    createUser("tenant_test", "123456")
            );

            Assert.assertNotNull(user);
            Assert.assertEquals("tenant_test", user.getUsername());
            Assert.assertEquals("Nguyen Van Test", user.getFullName());
            Assert.assertEquals("TENANT", user.getRole());
            Assert.assertEquals("ACTIVE", user.getStatus());
        }
    }

    @Test
    public void testCheckLoginStandard2() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            UserDAO dao = new UserDAO(con);

            User user = dao.checkLogin(
                    createUser("  tenant_test  ", "123456")
            );

            Assert.assertNotNull(user);
            Assert.assertEquals("tenant_test", user.getUsername());
        }
    }

    @Test
    public void testCheckLoginStandard3() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            UserDAO dao = new UserDAO(con);

            User user = dao.checkLogin(
                    createUser("tenant_test", "123456")
            );

            Assert.assertNotNull(user);

            List<Contract> contracts = user.getContracts();

            Assert.assertNotNull(contracts);
            Assert.assertEquals(1, contracts.size());
            Assert.assertEquals(
                    "TEST-HD-001",
                    contracts.get(0).getCode()
            );
        }
    }

    @Test
    public void testCheckLoginException1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            UserDAO dao = new UserDAO(con);

            User user = dao.checkLogin(
                    createUser("tenant_test", "wrong_password")
            );

            Assert.assertNull(user);
        }
    }

    @Test
    public void testCheckLoginException2() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            UserDAO dao = new UserDAO(con);

            User user = dao.checkLogin(
                    createUser("unknown_user", "123456")
            );

            Assert.assertNull(user);
        }
    }

    @Test
    public void testCheckLoginException3() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            UserDAO dao = new UserDAO(con);

            User user = dao.checkLogin(null);

            Assert.assertNull(user);
        }
    }
}
