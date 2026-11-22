package dao;

import util.DBConnection;
import util.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

public class DAO implements AutoCloseable {
    protected final Connection con;

    public DAO() {
        try {
            this.con = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public DAO(Connection con) {
        if (con == null) throw new IllegalArgumentException("Connection must not be null");
        this.con = con;
    }

    protected Connection getConnection() {
        return con;
    }

    public boolean beginTransaction() {
        try {
            con.setAutoCommit(false);
            return true;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void commit() {
        try {
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void rollback() {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (!con.isClosed()) con.close();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
