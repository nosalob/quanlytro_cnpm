package dao;

import entity.Building;
<<<<<<< HEAD
import java.sql.CallableStatement;
=======
import java.sql.Connection;
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
<<<<<<< HEAD
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
=======
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
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
        }
        return list;
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
