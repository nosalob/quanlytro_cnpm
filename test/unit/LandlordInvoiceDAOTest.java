package unit;

import dao.InvoiceDAO;
import entity.Invoice;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.util.List;

public class LandlordInvoiceDAOTest {
    @Test
    public void testSearchAllInvoices() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            List<Invoice> all = new InvoiceDAO(con).searchPendingPayment(0, 0, "", "Tất cả");
            Assert.assertTrue(all.stream().anyMatch(i -> "TEST-INV-09".equals(i.getCode())));
            Assert.assertTrue(all.stream().anyMatch(i -> "TEST-INV-10".equals(i.getCode())));
        }
    }

    @Test
    public void testSearchByBuildingRoomAndTime() throws Exception {
        try (Connection con = DBConnection.getConnection();
             var ps = con.prepareStatement("SELECT r.id, r.tblBuildingid FROM tblRoom r WHERE r.code='TEST-P101' LIMIT 1");
             var rs = ps.executeQuery()) {
            Assert.assertTrue(rs.next());
            int room = rs.getInt(1), building = rs.getInt(2);
            List<Invoice> found = new InvoiceDAO(con).searchPendingPayment(building, room, "09/2026", "Tất cả");
            Assert.assertTrue(found.stream().anyMatch(i -> "TEST-INV-09".equals(i.getCode())));
            Assert.assertFalse(found.stream().anyMatch(i -> "TEST-INV-10".equals(i.getCode())));
        }
    }

    @Test
    public void testSearchNoMatch() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            Assert.assertTrue(new InvoiceDAO(con).searchPendingPayment(-1, 2147483647, "", "Tất cả").isEmpty());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTime() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            new InvoiceDAO(con).searchPendingPayment(0, 0, "13/2026", "Tất cả");
        }
    }
}
