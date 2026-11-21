
package unit;

import dao.InvoiceItemDAO;
import entity.InvoiceItem;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class InvoiceItemDAOTest {

    @Test
    public void testGetInvoiceItemStandard1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            InvoiceItemDAO dao = new InvoiceItemDAO(con);
            int invoiceId = getTestInvoiceId(con);

            List<InvoiceItem> items = dao.getInvoiceItem(invoiceId);

            Assert.assertNotNull(items);
            Assert.assertEquals(3, items.size());
        }
    }

    @Test
    public void testGetInvoiceItemStandard2() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            InvoiceItemDAO dao = new InvoiceItemDAO(con);
            int invoiceId = getTestInvoiceId(con);

            List<InvoiceItem> items = dao.getInvoiceItem(invoiceId);

            Assert.assertNotNull(items);

            double total = 0;

            for (InvoiceItem item : items) {
                Assert.assertEquals(
                        item.getQuantity() * item.getUnitPrice(),
                        item.getAmount(),
                        0.01
                );

                total += item.getAmount();
            }

            Assert.assertEquals(3430000.0, total, 0.01);
        }
    }

    @Test
    public void testGetInvoiceItemException1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            InvoiceItemDAO dao = new InvoiceItemDAO(con);

            List<InvoiceItem> items = dao.getInvoiceItem(-1);

            Assert.assertNotNull(items);
            Assert.assertEquals(0, items.size());
        }
    }

    private int getTestInvoiceId(Connection con) throws Exception {
        String sql = "SELECT id FROM tblInvoice WHERE code = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "TEST-INV-09");

            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue(
                        "Không tìm thấy hóa đơn kiểm thử",
                        rs.next()
                );

                return rs.getInt("id");
            }
        }
    }
}
