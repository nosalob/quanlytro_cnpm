package test.unit;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import dao.BuildingDAO;
import model.Building;

public class BuildingDaoTest {
	BuildingDAO bd = new BuildingDAO();
	
	@Test
	public void testGetBuildingStandard(){//there are buildings, ordered by name
		ArrayList<Building> listBuilding = bd.getBuilding();
		Assert.assertNotNull(listBuilding);
		Assert.assertEquals(2, listBuilding.size());
		
		Assert.assertEquals(2, listBuilding.get(0).getId());
		Assert.assertEquals("NT02", listBuilding.get(0).getCode());
		Assert.assertEquals("Nha tro Binh Minh", listBuilding.get(0).getName());
		
		Assert.assertEquals(1, listBuilding.get(1).getId());
		Assert.assertEquals("NT01", listBuilding.get(1).getCode());
		Assert.assertEquals("Nha tro Hanh Phuc", listBuilding.get(1).getName());
		Assert.assertEquals("12 Xuan Thuy, Cau Giay, Ha Noi", listBuilding.get(1).getAddress());
		Assert.assertEquals("ACTIVE", listBuilding.get(1).getStatus());
	}
}
