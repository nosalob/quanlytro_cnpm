package dao;

import entity.PaymentProof;

import java.sql.*;

public class PaymentProofDAO extends DAO {
    public PaymentProofDAO() {
        super();
    }

    public PaymentProofDAO(Connection con) {
        super(con);
    }

    public boolean addPaymentProof(PaymentProof proof, int invoiceId, int userId) {
        String sql = "INSERT INTO tblPaymentProof " +
                "(imageUrl, declaredAmount, transferDate, note, status, submittedAt, tblInvoiceid, tblUserid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, proof.getImageUrl());
            ps.setDouble(2, proof.getDeclaredAmount());
            ps.setDate(3, proof.getTransferDate());
            ps.setString(4, proof.getNote());
            ps.setString(5, proof.getStatus());
            ps.setDate(6, proof.getSubmittedAt());
            ps.setInt(7, invoiceId);
            ps.setInt(8, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public PaymentProof getProofByInvoice(int invoiceId) {
        String sql = "SELECT id, imageUrl, declaredAmount, transferDate, note, status, rejectReason, " +
                "submittedAt, reviewedAt FROM tblPaymentProof WHERE tblInvoiceid = ? " +
                "ORDER BY CASE WHEN status = 'Chờ xác nhận' THEN 0 ELSE 1 END, submittedAt DESC, id DESC LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                PaymentProof proof = new PaymentProof();
                proof.setId(rs.getInt("id"));
                proof.setImageUrl(rs.getString("imageUrl"));
                proof.setDeclaredAmount(rs.getDouble("declaredAmount"));
                proof.setTransferDate(rs.getDate("transferDate"));
                proof.setNote(rs.getString("note"));
                proof.setStatus(rs.getString("status"));
                proof.setRejectReason(rs.getString("rejectReason"));
                proof.setSubmittedAt(rs.getDate("submittedAt"));
                proof.setReviewedAt(rs.getDate("reviewedAt"));
                return proof;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Không đọc được minh chứng", e);
        }
    }

    public boolean confirm(int paymentProofId, int reviewerId) {
        return changeProofStatus(paymentProofId, reviewerId, "Đã xác nhận", null);
    }

    public boolean reject(int paymentProofId, int reviewerId, String rejectReason) {
        if (rejectReason == null || rejectReason.isBlank()) return false;
        return changeProofStatus(paymentProofId, reviewerId, "Từ chối", rejectReason.trim());
    }


    private boolean changeProofStatus(int id, int reviewerId, String status, String reason) {
        String sql = "UPDATE tblPaymentProof p SET p.status=?, p.rejectReason=?, p.reviewedAt=CURRENT_DATE() " +
                "WHERE p.id=? AND p.status='Chờ xác nhận' " +
                "AND EXISTS (SELECT 1 FROM tblUser u WHERE u.id=? AND u.role='LANDLORD' AND u.status='ACTIVE')";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, id);
            ps.setInt(4, reviewerId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
