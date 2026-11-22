package dao;

import entity.Contract;

import java.sql.*;

public class ContractDAO extends DAO {
    public ContractDAO() {
        super();
    }

    public ContractDAO(Connection con) {
        super(con);
    }

    public Contract getActiveContractByRoom(int roomId) {
        String sql = "SELECT id, code, depositAmount, status FROM tblContract " +
                "WHERE tblRoomid = ? AND status = 'ACTIVE' LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract c = new Contract();
                    c.setId(rs.getInt("id"));
                    c.setCode(rs.getString("code"));
                    c.setDepositAmount(rs.getDouble("depositAmount"));
                    c.setStatus(rs.getString("status"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // số dư cọc = tiền cọc - các khoản bồi thường đã trừ vào cọc (của phòng này, từ lúc tạo hợp đồng)
    public double getDepositBalance(int contractId) {
        String sql = "SELECT c.depositAmount - COALESCE((" +
                "SELECT SUM(p.amount) FROM tblCompensation p " +
                "JOIN tblIncidentTicket t ON t.id = p.tblIncidentTicketid " +
                "WHERE t.tblRoomid = c.tblRoomid AND p.method = 'DEDUCT_DEPOSIT' " +
                "AND p.createdAt >= c.createdAt), 0) AS balance " +
                "FROM tblContract c WHERE c.id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
