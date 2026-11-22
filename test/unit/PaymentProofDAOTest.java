
package unit;

import dao.PaymentProofDAO;
import entity.PaymentProof;
import org.junit.Assert;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaymentProofDAOTest {

    @Test
    public void testAddPaymentProofStandard1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                PaymentProofDAO dao = new PaymentProofDAO(con);

                int invoiceId = getTestInvoiceId(con);
                int userId = getTestUserId(con);

                PaymentProof proof = createPaymentProof();

                boolean result = dao.addPaymentProof(
                    proof, invoiceId, userId
                );

                Assert.assertTrue(result);

                String sql = "SELECT imageUrl, declaredAmount, " +
                "transferDate, status, tblUserid " +
                "FROM tblPaymentProof " +
                "WHERE tblInvoiceid = ? AND imageUrl = ?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, invoiceId);
                    ps.setString(2, proof.getImageUrl());

                    try (ResultSet rs = ps.executeQuery()) {
                        Assert.assertTrue(rs.next());

                        Assert.assertEquals(
                            proof.getImageUrl(),
                            rs.getString("imageUrl")
                        );

                        Assert.assertEquals(
                            proof.getDeclaredAmount(),
                            rs.getDouble("declaredAmount"),
                            0.01
                        );

                        Assert.assertEquals(
                            proof.getTransferDate(),
                            rs.getDate("transferDate")
                        );

                        Assert.assertEquals(
                            "Chờ xác nhận",
                            rs.getString("status")
                        );

                        Assert.assertEquals(
                            userId,
                            rs.getInt("tblUserid")
                        );
                    }
                }
            } finally {
                con.rollback();
            }
        }
    }

    @Test
    public void testAddPaymentProofException1() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                PaymentProofDAO dao = new PaymentProofDAO(con);
                int userId = getTestUserId(con);

                boolean result = dao.addPaymentProof(
                    createPaymentProof(), -1, userId
                );

                Assert.assertFalse(result);
            } finally {
                con.rollback();
            }
        }
    }

    @Test
    public void testAddPaymentProofException2() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                PaymentProofDAO dao = new PaymentProofDAO(con);
                int invoiceId = getTestInvoiceId(con);

                boolean result = dao.addPaymentProof(
                    createPaymentProof(), invoiceId, -1
                );

                Assert.assertFalse(result);
            } finally {
                con.rollback();
            }
        }
    }

    private PaymentProof createPaymentProof() {
        PaymentProof proof = new PaymentProof();

        proof.setImageUrl("images/payment_proofs/junit_test.png");
        proof.setDeclaredAmount(3430000.0);
        proof.setTransferDate(Date.valueOf("2026-09-08"));
        proof.setNote("JUnit test");
        proof.setStatus("Chờ xác nhận");
        proof.setSubmittedAt(Date.valueOf("2026-09-08"));

        return proof;
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

    private int getTestUserId(Connection con) throws Exception {
        String sql = "SELECT id FROM tblUser WHERE username = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "tenant_test");

            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue(
                    "Không tìm thấy người dùng kiểm thử",
                    rs.next()
                );

                return rs.getInt("id");
            }
        }
    }
}
