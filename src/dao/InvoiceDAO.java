package dao;

import entity.Invoice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO extends DAO {
    public InvoiceDAO() { super(); }
    public InvoiceDAO(Connection con) {
        super(con);
    }
    public List<Invoice> getInvoicesByContract(List<Integer> contractIds) {
        List<Invoice> invoices = new ArrayList<>();
        if (contractIds == null || contractIds.isEmpty()) return invoices;

        StringBuilder sql = new StringBuilder(
                "SELECT i.id, i.code, i.month, i.year, i.dueDate, i.status, " +
                        "i.issuedAt, i.paidAt, COALESCE(SUM(ii.quantity * ii.unitPrice), 0) AS totalAmount " +
                        "FROM tblInvoice i LEFT JOIN tblInvoiceItem ii ON ii.tblInvoiceid = i.id " +
                        "WHERE i.tblContractid IN ("
        );
        for (int i = 0; i < contractIds.size(); i++) sql.append(i == 0 ? "?" : ", ?");
        sql.append(") GROUP BY i.id, i.code, i.month, i.year, i.dueDate, i.status, i.issuedAt, i.paidAt " +
                "ORDER BY i.year DESC, i.month DESC, i.id DESC");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < contractIds.size(); i++) ps.setInt(i + 1, contractIds.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setId(rs.getInt("id"));
                    invoice.setCode(rs.getString("code"));
                    invoice.setMonth(rs.getInt("month"));
                    invoice.setYear(rs.getInt("year"));
                    invoice.setDueDate(rs.getDate("dueDate"));
                    invoice.setStatus(rs.getString("status"));
                    invoice.setIssuedAt(rs.getDate("issuedAt"));
                    invoice.setPaidAt(rs.getDate("paidAt"));
                    invoice.setTotalAmount(rs.getDouble("totalAmount"));
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public boolean updateStatus(int invoiceId, String status) {
        String sql = "UPDATE tblInvoice SET status = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
