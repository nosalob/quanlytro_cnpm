package boundary;

import dao.InvoiceDAO;
import entity.Invoice;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchPendingPaymentFrm extends JFrame implements ActionListener {
    private final User user;
    private final JComboBox<String> cboBuilding = new JComboBox<>();
    private final JTextField txtRoom = new JTextField(8);
    private final JTextField txtTime = new JTextField(8);
    private final JComboBox<String> cboStatus = new JComboBox<>(new String[]{
            "Tất cả", "Chờ xác nhận", "Đã thanh toán", "Chua thanh toan"});
    private final JButton btnSearch = new JButton("Tìm kiếm");
    private final JButton btnInvoiceItem = new JButton("Xem minh chứng");
    private final JButton btnBack = new JButton("Quay lại");
    private final JTable tblInvoice;
    private List<Invoice> invoices = new ArrayList<>();

    public SearchPendingPaymentFrm(User user) {
        if (user == null || !"LANDLORD".equalsIgnoreCase(user.getRole()))
        throw new IllegalArgumentException("Chỉ chủ trọ được tìm minh chứng");
        this.user = user;
        setTitle("Tìm hóa đơn cần duyệt");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        cboBuilding.addItem("Tất cả");
        try {
            for (Integer id : new InvoiceDAO().getBuildingIds()) cboBuilding.addItem(String.valueOf(id));
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 10));
        filter.add(new JLabel("Mã tòa nhà (ID):"));
        filter.add(cboBuilding);
        filter.add(new JLabel("Mã phòng (ID):"));
        filter.add(txtRoom);
        filter.add(new JLabel("Tháng (MM/YYYY):"));
        filter.add(txtTime);
        filter.add(new JLabel("Trạng thái:"));
        filter.add(cboStatus);
        filter.add(btnSearch);
        add(filter, BorderLayout.NORTH);
        tblInvoice = new JTable(new DefaultTableModel(
                new String[]{"Mã", "Tháng", "Năm", "Hạn", "Tổng tiền", "Trạng thái"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
        });
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tblInvoice), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBack);
        bottom.add(btnInvoiceItem);
        add(bottom, BorderLayout.SOUTH);
        btnSearch.addActionListener(this);
        btnInvoiceItem.addActionListener(this);
        btnBack.addActionListener(this);
        btnSearchClick();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) btnSearchClick();
        else if (e.getSource() == btnInvoiceItem) btnInvoiceItemClick();
        else if (e.getSource() == btnBack) {
            new InvoiceListFrm(user).setVisible(true);
            dispose();
        }
    }

    private void btnSearchClick() {
        try {
            int buildingId = cboBuilding.getSelectedIndex() == 0 ? 0 :
            Integer.parseInt(String.valueOf(cboBuilding.getSelectedItem()));
            int roomId = txtRoom.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtRoom.getText().trim());
            if (roomId < 0) throw new IllegalArgumentException("Mã phòng không hợp lệ");
            invoices = new InvoiceDAO().searchPendingPayment(buildingId, roomId,
                txtTime.getText(), String.valueOf(cboStatus.getSelectedItem()));
            DefaultTableModel m = (DefaultTableModel) tblInvoice.getModel();
            m.setRowCount(0);
            DecimalFormat money = new DecimalFormat("#,##0.##");
            for (Invoice i : invoices)
            m.addRow(new Object[]{i.getCode(), i.getMonth(), i.getYear(),
                    i.getDueDate(), money.format(i.getTotalAmount()), i.getStatus()});
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã phòng phải là số nguyên (ID).");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void btnInvoiceItemClick() {
        int row = tblInvoice.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một hóa đơn.");
            return;
        }
        new ReviewPaymentProofFrm(user, invoices.get(tblInvoice.convertRowIndexToModel(row))).setVisible(true);
        dispose();
    }
}
