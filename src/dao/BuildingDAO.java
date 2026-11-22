package dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Building;

public class BuildingDAO extends DAO{
	
	public BuildingDAO() {
		super();
	}
	
	/**
	 * get all buildings in the tblBuilding
	 * @return list of buildings
	 */
	public ArrayList<Building> getBuilding(){
		ArrayList<Building> result = new ArrayList<Building>();
		String sql = "SELECT * FROM tblBuilding ORDER BY name";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				Building b = new Building();
				b.setId(rs.getInt("id"));
				b.setCode(rs.getString("code"));
				b.setName(rs.getString("name"));
				b.setAddress(rs.getString("address"));
				b.setStatus(rs.getString("status"));
				result.add(b);
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return result;
	}
}
