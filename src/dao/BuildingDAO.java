package dao;

import entity.Building;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BuildingDAO extends DAO {
    public BuildingDAO() {
        super();
    }

    public BuildingDAO(Connection con) {
        super(con);
    }

    public List<Building> getAllBuildings() {
        List<Building> list = new ArrayList<>();
        String sql = "SELECT id, code, name, address FROM tblBuilding ORDER BY name";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Building b = new Building();
                b.setId(rs.getInt("id"));
                b.setCode(rs.getString("code"));
                b.setName(rs.getString("name"));
                b.setAddress(rs.getString("address"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Building> getBuilding() {
        ArrayList<Building> result = new ArrayList<>();
        String sql = "SELECT * FROM tblBuilding ORDER BY name";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Building b = new Building();
                b.setId(rs.getInt("id"));
                b.setCode(rs.getString("code"));
                b.setName(rs.getString("name"));
                b.setAddress(rs.getString("address"));
                b.setStatus(rs.getString("status"));
                result.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}