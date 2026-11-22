package dao;

import entity.Resident;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import util.DataAccessException;
import util.PasswordUtil;

public class ResidentDAO extends DAO {

    public ArrayList<Resident> searchResident(String idCardNo) {
        ArrayList<Resident> list = new ArrayList<>();
        try (CallableStatement cs = con.prepareCall("{call spSearchResident(?)}")) {
            cs.setString(1, idCardNo);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Resident r = new Resident();
                    r.setId(rs.getInt("id"));
                    r.setUsername(rs.getString("username"));
                    r.setPasswordHash(rs.getString("passwordHash"));
                    r.setFullName(rs.getString("fullName"));
                    r.setPhone(rs.getString("phone"));
                    r.setEmail(rs.getString("email"));
                    r.setRole(rs.getString("role"));
                    r.setStatus(rs.getString("status"));
                    r.setIdCardNo(rs.getString("idCardNo"));
                    Date dob = rs.getDate("dateOfBirth");
                    if (dob != null) r.setDateOfBirth(dob.toLocalDate());
                    r.setHometown(rs.getString("hometown"));
                    r.setNote(rs.getString("note"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return list;
    }

    public boolean addResident(Resident r) {
        String sqlUser = "INSERT INTO tblUser(username, passwordHash, fullName, phone, email, role, status) "
        + "VALUES (?, ?, ?, ?, ?, 'TENANT', 'ACTIVE')";
        String sqlRes = "INSERT INTO tblResident(tblUserid, idCardNo, dateOfBirth, hometown, note) "
        + "VALUES (?, ?, ?, ?, ?)";
        try {
            con.setAutoCommit(false);
            String tempUser = r.getUsername() != null ? r.getUsername() : "res_" + r.getIdCardNo();
            String tempPass = r.getPasswordHash() != null ? r.getPasswordHash() : PasswordUtil.hash(r.getIdCardNo());
            String phone = r.getPhone() != null ? r.getPhone() : "";
            String email = r.getEmail() != null ? r.getEmail() : "";

            int userId;
            try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, tempUser);
                ps.setString(2, tempPass);
                ps.setString(3, r.getFullName());
                ps.setString(4, phone);
                ps.setString(5, email);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    userId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlRes)) {
                ps.setInt(1, userId);
                ps.setString(2, r.getIdCardNo());
                ps.setDate(3, Date.valueOf(r.getDateOfBirth()));
                ps.setString(4, r.getHometown());
                if (r.getNote() == null || r.getNote().isEmpty()) ps.setNull(5, Types.VARCHAR);
                else ps.setString(5, r.getNote());
                ps.executeUpdate();
            }

            con.commit();
            r.setId(userId);
            r.setUsername(tempUser);
            return true;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
            }
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }
}
