
package unit;

import dao.InvoiceDAO;
import entity.Invoice;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

public class InvoiceDAOTest {

    @Test
    public void testGetInvoicesByContractStandard1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            InvoiceDAO dao = new InvoiceDAO(con);

            int contractId = getTestContractId(con);

            List<Invoice> invoices = dao.getInvoicesByContract(
                Collections.singletonList(contractId)
            );

            Assert.assertNotNull(invoices);
            Assert.assertEquals(2, invoices.size());

            Assert.assertEquals("TEST-INV-10", invoices.get(0).getCode());
            Assert.assertEquals(
                3512500.0,
                invoices.get(0).getTotalAmount(),
                0.01
            );

            Assert.assertEquals("TEST-INV-09", invoices.get(1).getCode());
            Assert.assertEquals(
                3430000.0,
                invoices.get(1).getTotalAmount(),
                0.01
            );
        }
    }

    @Test
    public void testGetInvoicesByContractException1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            InvoiceDAO dao = new InvoiceDAO(con);

            List<Invoice> invoices = dao.getInvoicesByContract(
                Collections.emptyList()
            );

            Assert.assertNotNull(invoices);
            Assert.assertEquals(0, invoices.size());
        }
    }

    @Test
    public void testUpdateStatusStandard1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                InvoiceDAO dao = new InvoiceDAO(con);
                int invoiceId = getTestInvoiceId(con);

                boolean result = dao.updateStatus(
                    invoiceId, "Chờ xác nhận"
                );

                Assert.assertTrue(result);

                String sql = "SELECT status FROM tblInvoice WHERE id = ?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, invoiceId);

                    try (ResultSet rs = ps.executeQuery()) {
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals(
                            "Chờ xác nhận",
                            rs.getString("status")
                        );
                    }
                }
            } finally {
                con.rollback();
            }
        }
    }

    @Test
    public void testUpdateStatusException1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                InvoiceDAO dao = new InvoiceDAO(con);

                boolean result = dao.updateStatus(
                    -1, "Chờ xác nhận"
                );

                Assert.assertFalse(result);
            } finally {
                con.rollback();
            }
        }
    }

    private int getTestContractId(Connection con) throws Exception {
        String sql = "SELECT id FROM tblContract WHERE code = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "TEST-HD-001");

            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue(
                    "Không tìm thấy hợp đồng kiểm thử",
                    rs.next()
                );

                return rs.getInt("id");
            }
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
