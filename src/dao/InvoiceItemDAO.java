package dao;

import entity.InvoiceItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO extends DAO {
    public InvoiceItemDAO() {
        super();
    }

    public InvoiceItemDAO(Connection con) {
        super(con);
    }

    public List<InvoiceItem> getInvoiceItem(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT id, itemType, description, quantity, unitPrice " +
        "FROM tblInvoiceItem WHERE tblInvoiceid = ? ORDER BY id";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setId(rs.getInt("id"));
                    item.setItemType(rs.getString("itemType"));
                    item.setDescription(rs.getString("description"));
                    item.setQuantity(rs.getDouble("quantity"));
                    item.setUnitPrice(rs.getDouble("unitPrice"));
                    item.setAmount(item.getQuantity() * item.getUnitPrice());
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
