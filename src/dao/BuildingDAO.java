package dao;

import entity.Building;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import util.DataAccessException;

public class BuildingDAO extends DAO {
    public ArrayList<Building> getBuilding() {
        return getActiveBuildings();
    }

    public ArrayList<Building> getAllBuildings() {
        ArrayList<Building> list = new ArrayList<>();
        String sql = "SELECT id, code, name, address, status FROM tblBuilding ORDER BY name";
        try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(readBuilding(rs));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return list;
    }

    public ArrayList<Building> getActiveBuildings() {
        ArrayList<Building> list = new ArrayList<>();
        try (CallableStatement cs = con.prepareCall("{call spGetActiveBuildings()}");
            ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(readBuilding(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return list;
    }

    private Building readBuilding(ResultSet rs) throws SQLException {
        Building b = new Building();
        b.setId(rs.getInt("id"));
        b.setCode(rs.getString("code"));
        b.setName(rs.getString("name"));
        b.setAddress(rs.getString("address"));
        b.setStatus(rs.getString("status"));
        return b;
    }
}
