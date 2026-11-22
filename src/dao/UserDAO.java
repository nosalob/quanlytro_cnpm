package dao;

import entity.Contract;
import entity.Resident;
import entity.Room;
import entity.User;
import util.DataAccessException;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {
    public UserDAO() {
        super();
    }

    public UserDAO(Connection con) {
        super(con);
    }

    public User checkLogin(User inputUser) {
        if (inputUser == null || inputUser.getUsername() == null || inputUser.getPasswordHash() == null) return null;
        String sql = "SELECT id, username, passwordHash, fullName, phone, email, role, status " +
        "FROM tblUser WHERE username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, inputUser.getUsername().trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                if (!PasswordUtil.matches(inputUser.getPasswordHash(), rs.getString("passwordHash"))) return null;
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("passwordHash"));
                user.setFullName(rs.getString("fullName"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                if ("LANDLORD".equalsIgnoreCase(user.getRole())) {
                    user.setContracts(getAllContracts());
                } else {
                    user.setContracts(getContractsByUser(user.getId()));
                }
                return user;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void prepareTenantAccount(String buildingCode, Room room, Resident representative) {
        if (room == null || representative == null) {
            throw new IllegalArgumentException("Room and representative are required");
        }
        String base = ((buildingCode == null ? "" : buildingCode) + "-" + room.getCode())
        .toLowerCase().replaceAll("[^a-z0-9-]", "");
        if (base.isEmpty()) base = "tenant";
        String username = base;
        int suffix = 1;
        while (existsUsername(username)) username = base + (++suffix);

        String plainPassword = PasswordUtil.randomPassword(8);
        representative.setUsername(username);
        representative.setTempPassword(plainPassword);
        representative.setPasswordHash(PasswordUtil.hash(plainPassword));
        representative.setRole("TENANT");
        representative.setStatus("ACTIVE");
    }

    private boolean existsUsername(String username) {
        String sql = "SELECT COUNT(*) FROM tblUser WHERE username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private List<Contract> getContractsByUser(int userId) throws SQLException {
        String sql = "SELECT DISTINCT c.id, c.code FROM tblResident r " +
        "JOIN tblOccupancy o ON o.tblResidentid = r.idCardNo " +
        "JOIN tblContract c ON c.id = o.tblContractid " +
        "WHERE r.tblUserid = ? ORDER BY c.id";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return readContracts(rs);
            }
        }
    }


    private List<Contract> getAllContracts() throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT id, code FROM tblContract ORDER BY id");
            ResultSet rs = ps.executeQuery()) {
            return readContracts(rs);
        }
    }

    private List<Contract> readContracts(ResultSet rs) throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        while (rs.next()) {
            Contract c = new Contract();
            c.setId(rs.getInt("id"));
            c.setCode(rs.getString("code"));
            contracts.add(c);
        }
        return contracts;
    }
}
