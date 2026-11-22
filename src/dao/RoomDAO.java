package dao;

import entity.Room;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import util.DataAccessException;

public class RoomDAO extends DAO {

    public ArrayList<Room> searchVacantRoom(int buildingId, String keyword) {
        ArrayList<Room> list = new ArrayList<>();
        try (CallableStatement cs = con.prepareCall("{call spSearchVacantRoom(?, ?)}")) {
            cs.setInt(1, buildingId);
            cs.setString(2, keyword == null ? "" : keyword);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setId(rs.getInt("id"));
                    r.setCode(rs.getString("code"));
                    r.setArea(rs.getFloat("area"));
                    r.setRentPrice(rs.getFloat("rentPrice"));
                    r.setDepositAmount(rs.getFloat("depositAmount"));
                    r.setMaxOccupants(rs.getInt("maxOccupants"));
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return list;
    }

    public boolean checkAvailable(int roomId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) return false;
        try (CallableStatement cs = con.prepareCall("{call spCountRoomConflicts(?, ?, ?)}")) {
            cs.setInt(1, roomId);
            cs.setDate(2, Date.valueOf(startDate));
            cs.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = cs.executeQuery()) {
                return rs.next() && rs.getInt("conflicts") == 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean checkCapacity(int roomId, int occupantCount) {
        String sql = "SELECT maxOccupants FROM tblRoom WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && occupantCount <= rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean updateStatus(int roomId, String status) {
        String sql = "UPDATE tblRoom SET status = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
