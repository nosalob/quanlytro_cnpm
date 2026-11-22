package dao;

import entity.Contract;
import entity.ContractTerm;
import entity.Occupancy;
import entity.Resident;
import entity.Room;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ContractDAO extends DAO {

    public Contract getActiveContractByRoom(int roomId) {
        String sql = "SELECT id, code, depositAmount, status FROM tblContract " +
        "WHERE tblRoomid = ? AND status = 'ACTIVE' ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Contract c = new Contract();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setDepositAmount(rs.getDouble("depositAmount"));
                c.setStatus(rs.getString("status"));
                return c;
            }
        } catch (SQLException e) {
            throw new util.DataAccessException(e);
        }
    }

    public double getDepositBalance(int contractId) {
        String sql = "SELECT c.depositAmount - COALESCE((" +
        "SELECT SUM(p.amount) FROM tblCompensation p " +
        "JOIN tblIncidentTicket t ON t.id = p.tblIncidentTicketid " +
        "WHERE t.tblRoomid = c.tblRoomid AND p.method = 'DEDUCT_DEPOSIT' " +
        "AND p.createdAt >= c.createdAt), 0) AS balance " +
        "FROM tblContract c WHERE c.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("balance") : 0;
            }
        } catch (SQLException e) {
            throw new util.DataAccessException(e);
        }
    }

    public boolean addContract(Contract c, Room room) {
        if (c == null || room == null || c.getTerms().isEmpty() || c.getOccupancies().isEmpty()) return false;
        ContractTerm term = c.getTerms().get(0);

        Resident rep = null;
        for (Occupancy o : c.getOccupancies()) {
            if (o.isRepresentative()) rep = o.getResident();
        }
        if (rep == null || c.getOccupancies().size() > room.getMaxOccupants()) return false;

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement("SELECT status FROM tblRoom WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, room.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !"VACANT".equals(rs.getString(1))) {
                        con.rollback();
                        return false;
                    }
                }
            }

            String sqlUpdateUser = "UPDATE tblUser SET username = ?, passwordHash = ?, role = 'TENANT', status = 'ACTIVE' WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateUser)) {
                ps.setString(1, rep.getUsername());
                ps.setString(2, rep.getPasswordHash());
                ps.setInt(3, rep.getId());
                ps.executeUpdate();
            }

            String code = "HD-" + room.getId() + "-" + term.getStartDate().format(DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate today = LocalDate.now();
            int contractId;
            String sqlContract = "INSERT INTO tblContract(code, depositAmount, status, createdAt, tblRoomid) "
            + "VALUES (?, ?, 'ACTIVE', ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlContract, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, code);
                ps.setFloat(2, c.getDepositAmount());
                ps.setDate(3, Date.valueOf(today));
                ps.setInt(4, room.getId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    contractId = keys.getInt(1);
                }
            }

            String sqlTerm = "INSERT INTO tblContractTerm(termNo, startDate, endDate, rentPrice, status, tblContractid) "
            + "VALUES (1, ?, ?, ?, 'ACTIVE', ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlTerm)) {
                ps.setDate(1, Date.valueOf(term.getStartDate()));
                ps.setDate(2, Date.valueOf(term.getEndDate()));
                ps.setFloat(3, term.getRentPrice());
                ps.setInt(4, contractId);
                ps.executeUpdate();
            }

            String sqlOcc = "INSERT INTO tblOccupancy(isRepresentative, moveInDate, tblContractid, tblResidentid) "
            + "VALUES (?, ?, ?, ?)";
            for (Occupancy o : c.getOccupancies()) {
                try (PreparedStatement ps = con.prepareStatement(sqlOcc)) {
                    ps.setInt(1, o.getIsRepresentative());
                    ps.setDate(2, Date.valueOf(term.getStartDate()));
                    ps.setInt(3, contractId);
                    ps.setString(4, o.getResident().getIdCardNo());
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE tblRoom SET status = 'OCCUPIED' WHERE id = ?")) {
                ps.setInt(1, room.getId());
                ps.executeUpdate();
            }

            con.commit();
            c.setId(contractId);
            c.setCode(code);
            c.setCreatedAt(today);
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
