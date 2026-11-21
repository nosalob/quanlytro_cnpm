package boundary;

import dao.InvoiceDAO;
import entity.Contract;
import entity.Invoice;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceListFrm extends JFrame implements ActionListener {
    private final User user;
    private JButton btnInvoiceItem, btnSearch, btnBack;
    private JTable tblInvoice;
    private JLabel lblStatus;
    private List<Invoice> invoices = new ArrayList<>();

    public InvoiceListFrm(User user) {
        this.user = user;
        setTitle("Danh sách hóa đơn");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadInvoices();
    }

    private boolean isLandlord() {
        return user != null && "LANDLORD".equalsIgnoreCase(user.getRole());
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("DANH SÁCH HÓA ĐƠN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Mã hóa đơn", "Tháng", "Năm", "Hạn thanh toán", "Tổng tiền", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblInvoice = new JTable(model);
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tblInvoice), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        lblStatus = new JLabel(" ");
        bottom.add(lblStatus, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại");
        btnSearch = new JButton(isLandlord() ? "Tìm hóa đơn cần duyệt" : "Làm mới");
        btnInvoiceItem = new JButton(isLandlord() ? "Xem minh chứng" : "Xem chi tiết hóa đơn");
        for (JButton b : new JButton[]{btnBack, btnSearch, btnInvoiceItem}) {
            b.addActionListener(this);
            buttons.add(b);
        }
        bottom.add(buttons, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadInvoices() {
        List<Integer> ids = new ArrayList<>();
        if (user != null && user.getContracts() != null)
            for (Contract c : user.getContracts()) if (c != null) ids.add(c.getId());
        invoices = new InvoiceDAO().getInvoicesByContract(ids);
        DefaultTableModel model = (DefaultTableModel) tblInvoice.getModel();
        model.setRowCount(0);
        DecimalFormat money = new DecimalFormat("#,##0.##");
        for (Invoice i : invoices)
            model.addRow(new Object[]{i.getCode(), i.getMonth(), i.getYear(),
                    i.getDueDate(), money.format(i.getTotalAmount()), i.getStatus()});
        lblStatus.setText("Số hóa đơn: " + invoices.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) btnSearchClick();
        else if (e.getSource() == btnInvoiceItem) btnInvoiceItemClick();
        else if (e.getSource() == btnBack) {
            if (isLandlord()) new LandlordHomeFrm(user).setVisible(true);
            else new TenantHomeFrm(user).setVisible(true);
            dispose();
        }
    }

    public void btnSearchClick() {
        if (isLandlord()) {
            new SearchPendingPaymentFrm(user).setVisible(true);
            dispose();
        } else loadInvoices();
    }

    public void btnInvoiceItemClick() {
        int row = tblInvoice.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn.");
            return;
        }
        Invoice selected = invoices.get(tblInvoice.convertRowIndexToModel(row));
        if (isLandlord()) new ReviewPaymentProofFrm(user, selected).setVisible(true);
        else new InvoiceItemListFrm(user, selected).setVisible(true);
        dispose();
    }
}
