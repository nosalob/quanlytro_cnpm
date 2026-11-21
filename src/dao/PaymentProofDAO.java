package dao;

import entity.PaymentProof;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentProofDAO extends DAO {
    public PaymentProofDAO() {
        super();
    }
    public PaymentProofDAO(Connection con) {
        super(con);
    }
    public boolean addPaymentProof(PaymentProof paymentProof, int invoiceId, int userId) {
        String sql = "INSERT INTO tblPaymentProof " +
                "(imageUrl, declaredAmount, transferDate, note, status, submittedAt, tblInvoiceid, tblUserid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, paymentProof.getImageUrl());
            ps.setDouble(2, paymentProof.getDeclaredAmount());
            ps.setDate(3, paymentProof.getTransferDate());
            ps.setString(4, paymentProof.getNote());
            ps.setString(5, paymentProof.getStatus());
            ps.setDate(6, paymentProof.getSubmittedAt());
            ps.setInt(7, invoiceId);
            ps.setInt(8, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
