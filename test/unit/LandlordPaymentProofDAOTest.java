package unit;

import dao.InvoiceDAO;
import dao.PaymentProofDAO;
import entity.PaymentProof;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.*;

public class LandlordPaymentProofDAOTest {
    private int id(Connection con, String sql, String code) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue("Thiếu dữ liệu mẫu: " + code, rs.next());
                return rs.getInt(1);
            }
        }
    }

    private int insertProof(Connection con, int invoiceId, int tenantId) throws SQLException {
        String sql = "INSERT INTO tblPaymentProof (imageUrl, declaredAmount, transferDate, note, " +
        "status, submittedAt, tblInvoiceid, tblUserid) VALUES (?,?,?,?,'Chờ xác nhận',CURRENT_DATE(),?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "images/junit.png");
            ps.setDouble(2, 3430000);
            ps.setDate(3, Date.valueOf("2026-09-08"));
            ps.setString(4, "JUnit");
            ps.setInt(5, invoiceId);
            ps.setInt(6, tenantId);
            Assert.assertEquals(1, ps.executeUpdate());
            try (ResultSet rs = ps.getGeneratedKeys()) {
                Assert.assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    @Test
    public void testGetProofByInvoice() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            int invoiceId = id(con, "SELECT id FROM tblInvoice WHERE code=?", "TEST-INV-09");
            PaymentProof proof = new PaymentProofDAO(con).getProofByInvoice(invoiceId);
            Assert.assertNotNull(proof);
            Assert.assertEquals("Chờ xác nhận", proof.getStatus());
            Assert.assertTrue(proof.getId() > 0);
        }
    }

    @Test
    public void testGetProofNotFound() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            Assert.assertNull(new PaymentProofDAO(con).getProofByInvoice(-1));
        }
    }

    @Test
    public void testConfirmAndUpdateInvoiceInOneTransaction() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int invoice = id(con, "SELECT id FROM tblInvoice WHERE code=?", "TEST-INV-10");
                int tenant = id(con, "SELECT id FROM tblUser WHERE username=?", "tenant_test");
                int landlord = id(con, "SELECT id FROM tblUser WHERE username=?", "landlord_test");
                int proof = insertProof(con, invoice, tenant);
                Assert.assertTrue(new PaymentProofDAO(con).confirm(proof, landlord));
                Assert.assertTrue(new InvoiceDAO(con).updateStatus(invoice, "Đã thanh toán"));
                try (PreparedStatement ps = con.prepareStatement("SELECT status, reviewedAt FROM tblPaymentProof WHERE id=?")) {
                    ps.setInt(1, proof);
                    try (ResultSet rs = ps.executeQuery()) {
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("Đã xác nhận", rs.getString(1));
                        Assert.assertNotNull(rs.getDate(2));
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("SELECT status, paidAt FROM tblInvoice WHERE id=?")) {
                    ps.setInt(1, invoice);
                    try (ResultSet rs = ps.executeQuery()) {
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("Đã thanh toán", rs.getString(1));
                        Assert.assertNotNull(rs.getDate(2));
                    }
                }
                Assert.assertFalse("Không được duyệt hai lần", new PaymentProofDAO(con).confirm(proof, landlord));
            } finally {
                con.rollback();
            }
        }
    }

    @Test
    public void testRejectProofAndRevertInvoice() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int invoice = id(con, "SELECT id FROM tblInvoice WHERE code=?", "TEST-INV-10");
                int tenant = id(con, "SELECT id FROM tblUser WHERE username=?", "tenant_test");
                int landlord = id(con, "SELECT id FROM tblUser WHERE username=?", "landlord_test");
                int proof = insertProof(con, invoice, tenant);
                Assert.assertTrue(new PaymentProofDAO(con).reject(proof, landlord, "Ảnh không rõ"));
                Assert.assertTrue(new InvoiceDAO(con).updateStatus(invoice, "Chua thanh toan"));
                try (PreparedStatement ps = con.prepareStatement("SELECT status, rejectReason FROM tblPaymentProof WHERE id=?")) {
                    ps.setInt(1, proof);
                    try (ResultSet rs = ps.executeQuery()) {
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("Từ chối", rs.getString(1));
                        Assert.assertEquals("Ảnh không rõ", rs.getString(2));
                    }
                }
                Assert.assertFalse(new PaymentProofDAO(con).reject(proof, landlord, "Lần nữa"));
            } finally {
                con.rollback();
            }
        }
    }

    @Test
    public void testRejectRequiresReason() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            int landlord = id(con, "SELECT id FROM tblUser WHERE username=?", "landlord_test");
            Assert.assertFalse(new PaymentProofDAO(con).reject(-1, landlord, " "));
        }
    }

    @Test
    public void testNonLandlordCannotConfirm() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int invoice = id(con, "SELECT id FROM tblInvoice WHERE code=?", "TEST-INV-10");
                int tenant = id(con, "SELECT id FROM tblUser WHERE username=?", "tenant_test");
                int proof = insertProof(con, invoice, tenant);
                Assert.assertFalse(new PaymentProofDAO(con).confirm(proof, tenant));
            } finally {
                con.rollback();
            }
        }
    }
}
