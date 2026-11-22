package dao;

import entity.Compensation;

import java.sql.*;

public class CompensationDAO extends DAO {
    public CompensationDAO() {
        super();
    }

    public CompensationDAO(Connection con) {
        super(con);
    }

    // bảng tblCompensation chỉ nối với ticket (không có cột hợp đồng)
    public boolean addCompensation(int ticketId, Compensation comp) {
        String sql = "INSERT INTO tblCompensation(amount, reason, method, status, createdAt, settledAt, tblIncidentTicketid) " +
<<<<<<< HEAD
        "VALUES (?, ?, ?, ?, CURDATE(), IF(? = 'SETTLED', CURDATE(), NULL), ?)";
=======
                "VALUES (?, ?, ?, ?, CURDATE(), IF(? = 'SETTLED', CURDATE(), NULL), ?)";
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, comp.getAmount());
            ps.setString(2, comp.getReason());
            ps.setString(3, comp.getMethod());
            ps.setString(4, comp.getStatus());
            ps.setString(5, comp.getStatus());
            ps.setInt(6, ticketId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
