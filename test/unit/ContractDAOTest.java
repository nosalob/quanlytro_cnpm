package unit;

import dao.ContractDAO;
import entity.Contract;
import entity.ContractTerm;
import entity.Occupancy;
import entity.Resident;
import entity.Room;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

public class ContractDAOTest {
    private int buildingId;
    private int roomId;
    private int userId;
    private String username;
    private String idCardNo;
    private Room room;

    @Before
    public void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        username = "contract_test_" + suffix;
        idCardNo = "TEST-ID-" + suffix;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                buildingId = insertBuilding(con, "TEST-BLD-" + suffix);
                roomId = insertRoom(con, "TEST-ROOM-" + suffix, buildingId);
                userId = insertUser(con, username);
                insertResident(con, idCardNo, userId);
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }

        room = new Room();
        room.setId(roomId);
        room.setMaxOccupants(2);
    }

    @After
    public void tearDown() throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                deleteByRoom(con, "tblOccupancy", roomId);
                deleteByRoom(con, "tblContractTerm", roomId);
                deleteContracts(con, roomId);
                deleteById(con, "tblResident", "tblUserid", userId);
                deleteById(con, "tblUser", "id", userId);
                deleteById(con, "tblRoom", "id", roomId);
                deleteById(con, "tblBuilding", "id", buildingId);
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    @Test
    public void addContract_createsContractTermOccupancyAndUpdatesRoomAndRepresentative() throws Exception {
        Contract contract = createContract(true, 1);

        try (ContractDAO dao = new ContractDAO()) {
            Assert.assertTrue(dao.addContract(contract, room));
        }

        Assert.assertTrue(contract.getId() > 0);
        Assert.assertEquals("HD-" + roomId + "-" + contract.getTerms().get(0)
                .getStartDate().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE), contract.getCode());

        try (Connection con = DBConnection.getConnection()) {
            Assert.assertEquals("OCCUPIED", getRoomStatus(con));
            Assert.assertEquals("TENANT", getUserRole(con));
            Assert.assertEquals(1, countRows(con,
                    "SELECT COUNT(*) FROM tblContractTerm WHERE tblContractid = ?", contract.getId()));
            Assert.assertEquals(1, countRows(con,
                    "SELECT COUNT(*) FROM tblOccupancy WHERE tblContractid = ? AND isRepresentative = 1",
                    contract.getId()));
        }
    }

    @Test
    public void addContract_rejectsContractWithoutRepresentative() {
        Contract contract = createContract(false, 1);

        try (ContractDAO dao = new ContractDAO()) {
            Assert.assertFalse(dao.addContract(contract, room));
        }
    }

    @Test
    public void addContract_rejectsMoreOccupantsThanRoomCapacity() {
        room.setMaxOccupants(1);
        Contract contract = createContract(true, 2);

        try (ContractDAO dao = new ContractDAO()) {
            Assert.assertFalse(dao.addContract(contract, room));
        }
    }

    @Test
    public void addContract_rejectsRoomThatIsNotVacant() throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE tblRoom SET status = 'OCCUPIED' WHERE id = ?")) {
            ps.setInt(1, roomId);
            Assert.assertEquals(1, ps.executeUpdate());
        }

        Contract contract = createContract(true, 1);
        try (ContractDAO dao = new ContractDAO()) {
            Assert.assertFalse(dao.addContract(contract, room));
        }

        try (Connection con = DBConnection.getConnection()) {
            Assert.assertEquals(0, countRows(con,
                    "SELECT COUNT(*) FROM tblContract WHERE tblRoomid = ?", roomId));
            Assert.assertEquals("PENDING", getUserRole(con));
        }
    }

    @Test
    public void addContract_rejectsMissingContractData() {
        try (ContractDAO dao = new ContractDAO()) {
            Assert.assertFalse(dao.addContract(null, room));
            Assert.assertFalse(dao.addContract(new Contract(), room));
            Assert.assertFalse(dao.addContract(createContract(true, 1), null));
        }
    }

    private Contract createContract(boolean hasRepresentative, int occupantCount) {
        LocalDate startDate = LocalDate.now().plusDays(1);
        ContractTerm term = new ContractTerm();
        term.setStartDate(startDate);
        term.setEndDate(startDate.plusYears(1).minusDays(1));
        term.setRentPrice(3_000_000);
        term.setStatus("ACTIVE");

        Resident resident = new Resident();
        resident.setId(userId);
        resident.setIdCardNo(idCardNo);
        resident.setUsername(username);
        resident.setPasswordHash("updated-test-password");

        Contract contract = new Contract();
        contract.setDepositAmount(3_000_000);
        contract.getTerms().add(term);
        for (int i = 0; i < occupantCount; i++) {
            Occupancy occupancy = new Occupancy();
            occupancy.setRepresentative(hasRepresentative && i == 0);
            occupancy.setResident(resident);
            contract.getOccupancies().add(occupancy);
        }
        return contract;
    }

    private int insertBuilding(Connection con, String code) throws Exception {
        String sql = "INSERT INTO tblBuilding(code, name, address, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, "Contract DAO test building");
            ps.setString(3, "Test address");
            ps.executeUpdate();
            return generatedId(ps);
        }
    }

    private int insertRoom(Connection con, String code, int buildingId) throws Exception {
        String sql = "INSERT INTO tblRoom(code, area, rentPrice, depositAmount, maxOccupants, status, tblBuildingid) "
                + "VALUES (?, 25, 3000000, 3000000, 2, 'VACANT', ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setInt(2, buildingId);
            ps.executeUpdate();
            return generatedId(ps);
        }
    }

    private int insertUser(Connection con, String username) throws Exception {
        String sql = "INSERT INTO tblUser(username, passwordHash, fullName, phone, email, role, status) "
                + "VALUES (?, 'old-test-password', 'Contract Test Resident', '0900000000', ?, 'PENDING', 'INACTIVE')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, username + "@example.test");
            ps.executeUpdate();
            return generatedId(ps);
        }
    }

    private void insertResident(Connection con, String idCardNo, int userId) throws Exception {
        String sql = "INSERT INTO tblResident(idCardNo, dateOfBirth, hometown, note, tblUserid) "
                + "VALUES (?, '2000-01-01', 'Test hometown', NULL, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idCardNo);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private int generatedId(PreparedStatement ps) throws Exception {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (!rs.next()) throw new AssertionError("Insert did not return a generated ID");
            return rs.getInt(1);
        }
    }

    private String getRoomStatus(Connection con) throws Exception {
        return getString(con, "SELECT status FROM tblRoom WHERE id = ?", roomId);
    }

    private String getUserRole(Connection con) throws Exception {
        return getString(con, "SELECT role FROM tblUser WHERE id = ?", userId);
    }

    private String getString(Connection con, String sql, int id) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue(rs.next());
                return rs.getString(1);
            }
        }
    }

    private int countRows(Connection con, String sql, int id) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private void deleteByRoom(Connection con, String table, int roomId) throws Exception {
        String sql = "DELETE FROM " + table + " WHERE tblContractid IN "
                + "(SELECT id FROM tblContract WHERE tblRoomid = ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }

    private void deleteContracts(Connection con, int roomId) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM tblContract WHERE tblRoomid = ?")) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }

    private void deleteById(Connection con, String table, String column, int id) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + table + " WHERE " + column + " = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
