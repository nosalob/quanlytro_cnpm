package dao;

import entity.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO extends DAO {
    public InvoiceDAO() {
        super();
    }

    public InvoiceDAO(Connection con) {
        super(con);
    }

    public List<Invoice> getInvoicesByContract(List<Integer> contractIds) {
        List<Invoice> invoices = new ArrayList<>();
        if (contractIds == null || contractIds.isEmpty()) return invoices;
        StringBuilder sql = new StringBuilder("SELECT i.id, i.code, i.month, i.year, i.dueDate, i.status, " +
                "i.issuedAt, i.paidAt, COALESCE(SUM(ii.quantity * ii.unitPrice), 0) AS totalAmount " +
                "FROM tblInvoice i LEFT JOIN tblInvoiceItem ii ON ii.tblInvoiceid = i.id " +
                "WHERE i.tblContractid IN (");
        for (int i = 0; i < contractIds.size(); i++) sql.append(i == 0 ? "?" : ", ?");
        sql.append(") GROUP BY i.id, i.code, i.month, i.year, i.dueDate, i.status, i.issuedAt, i.paidAt " +
                "ORDER BY i.year DESC, i.month DESC, i.id DESC");
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < contractIds.size(); i++) ps.setInt(i + 1, contractIds.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) invoices.add(readInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public List<Invoice> searchPendingPayment(int buildingId, int roomId, String time, String status) {
        List<Invoice> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.id, i.code, i.month, i.year, i.dueDate, i.status, i.issuedAt, i.paidAt, " +
                        "COALESCE(SUM(ii.quantity * ii.unitPrice), 0) AS totalAmount " +
                        "FROM tblInvoice i JOIN tblContract c ON c.id = i.tblContractid " +
                        "JOIN tblRoom r ON r.id = c.tblRoomid " +
                        "LEFT JOIN tblInvoiceItem ii ON ii.tblInvoiceid = i.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (buildingId > 0) {
            sql.append(" AND r.tblBuildingid = ?");
            params.add(buildingId);
        }
        if (roomId > 0) {
            sql.append(" AND r.id = ?");
            params.add(roomId);
        }
        if (time != null && !time.isBlank()) {
            String[] parts = time.trim().split("/");
            if (parts.length != 2) throw new IllegalArgumentException("Thời gian phải là MM/YYYY");
            int month, year;
            try {
                month = Integer.parseInt(parts[0].trim());
                year = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Thời gian phải là MM/YYYY", e);
            }
            if (month < 1 || month > 12 || year < 1) throw new IllegalArgumentException("Tháng/năm không hợp lệ");
            sql.append(" AND i.month = ? AND i.year = ?");
            params.add(month);
            params.add(year);
        }
        if (status != null && !status.isBlank() && !"Tất cả".equalsIgnoreCase(status)) {
            sql.append(" AND i.status = ?");
            params.add(status);
        }
        sql.append(" GROUP BY i.id, i.code, i.month, i.year, i.dueDate, i.status, i.issuedAt, i.paidAt " +
                "ORDER BY i.year DESC, i.month DESC, i.id DESC");
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(readInvoice(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể tìm hóa đơn", e);
        }
        return result;
    }

    public boolean updateStatus(int invoiceId, String status) {
        String sql = "UPDATE tblInvoice SET status = ?, paidAt = CASE WHEN ? = 'Đã thanh toán' " +
                "THEN CURRENT_DATE() ELSE NULL END WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, invoiceId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getBuildingIds() {
        List<Integer> ids = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT id FROM tblBuilding ORDER BY id")) {
            while (rs.next()) ids.add(rs.getInt(1));
        } catch (SQLException e) {
            throw new IllegalStateException("Không lấy được tòa nhà", e);
        }
        return ids;
    }

    private Invoice readInvoice(ResultSet rs) throws SQLException {
        Invoice v = new Invoice();
        v.setId(rs.getInt("id"));
        v.setCode(rs.getString("code"));
        v.setMonth(rs.getInt("month"));
        v.setYear(rs.getInt("year"));
        v.setDueDate(rs.getDate("dueDate"));
        v.setStatus(rs.getString("status"));
        v.setIssuedAt(rs.getDate("issuedAt"));
        v.setPaidAt(rs.getDate("paidAt"));
        v.setTotalAmount(rs.getDouble("totalAmount"));
        return v;
    }
}
