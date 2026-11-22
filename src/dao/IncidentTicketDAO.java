package dao;

import entity.Building;
import entity.IncidentTicket;
import entity.Room;
import entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentTicketDAO extends DAO {
    private static final String SELECT_TICKET =
            "SELECT t.id, t.code, t.title, t.description, t.imageUrl, t.status, t.faultType, " +
            "t.resolutionNote, t.createdAt, t.resolvedAt, " +
            "r.id AS roomId, r.code AS roomCode, r.tblBuildingid AS buildingId, " +
            "u.id AS userId, u.fullName, u.phone " +
            "FROM tblIncidentTicket t " +
            "JOIN tblRoom r ON r.id = t.tblRoomid " +
            "JOIN tblResident rs ON rs.idCardNo = t.tblResidentid " +
            "JOIN tblUser u ON u.id = rs.tblUserid ";

    public IncidentTicketDAO() {
        super();
    }

    public IncidentTicketDAO(Connection con) {
        super(con);
    }

    public List<IncidentTicket> searchTicket(int buildingId, String status) {
        List<IncidentTicket> list = new ArrayList<>();
        String sql = SELECT_TICKET + "WHERE r.tblBuildingid = ? AND t.status = ? ORDER BY t.createdAt, t.id";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, buildingId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(readTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public IncidentTicket getTicketDetail(int ticketId) {
        String sql = SELECT_TICKET + "WHERE t.id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readTicket(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // faultType trong DB là NOT NULL nên chưa phân loại thì lưu "NONE"
    public boolean updateStatus(int ticketId, String status, String faultType, String note) {
        String sql = "UPDATE tblIncidentTicket SET status = ?, faultType = ?, resolutionNote = ?, " +
                "resolvedAt = IF(? = 'RESOLVED', CURDATE(), NULL) WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, faultType == null ? "NONE" : faultType);
            ps.setString(3, note);
            ps.setString(4, status);
            ps.setInt(5, ticketId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private IncidentTicket readTicket(ResultSet rs) throws SQLException {
        IncidentTicket t = new IncidentTicket();
        t.setId(rs.getInt("id"));
        t.setCode(rs.getString("code"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setImageUrl(rs.getString("imageUrl"));
        t.setStatus(rs.getString("status"));
        t.setFaultType(rs.getString("faultType"));
        t.setResolutionNote(rs.getString("resolutionNote"));
        t.setCreatedAt(rs.getDate("createdAt"));
        t.setResolvedAt(rs.getDate("resolvedAt"));

        Building b = new Building();
        b.setId(rs.getInt("buildingId"));
        Room r = new Room();
        r.setId(rs.getInt("roomId"));
        r.setCode(rs.getString("roomCode"));
        r.setBuilding(b);
        t.setRoom(r);

        User u = new User();
        u.setId(rs.getInt("userId"));
        u.setFullName(rs.getString("fullName"));
        u.setPhone(rs.getString("phone"));
        t.setUser(u);
        return t;
    }
}
