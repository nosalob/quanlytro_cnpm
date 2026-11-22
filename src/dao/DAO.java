package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DAO {
    private final Connection con;

    public DAO() {
        try {
            con = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể kết nối đến cơ sở dữ liệu.", e);
        }
    }

    public DAO(Connection con) {
        this.con = con;
    }

    protected Connection getConnection() {
        return con;
    }
}