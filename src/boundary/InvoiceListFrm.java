package boundary;

import dao.InvoiceDAO;
import entity.Contract;
import entity.Invoice;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceListFrm extends JFrame implements ActionListener {
    private JButton btnInvoiceItem;
    private JTable tblInvoice;
    private JLabel lblStatus;
    private JButton btnSearch;
    private User user;
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

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("DANH SÁCH HÓA ĐƠN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Mã hóa đơn", "Tháng", "Năm", "Hạn thanh toán", "Tổng tiền", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblInvoice = new JTable(model);
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tblInvoice), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        lblStatus = new JLabel(" ");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSearch = new JButton("Làm mới");
        btnInvoiceItem = new JButton("Xem chi tiết hóa đơn");
        btnSearch.addActionListener(this);
        btnInvoiceItem.addActionListener(this);
        buttons.add(btnSearch);
        buttons.add(btnInvoiceItem);
        bottom.add(lblStatus, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadInvoices() {
        List<Integer> contractIds = new ArrayList<>();
        if (user != null && user.getContracts() != null) {
            for (Contract contract : user.getContracts()) {
                if (contract != null) contractIds.add(contract.getId());
            }
        }

        invoices = new InvoiceDAO().getInvoicesByContract(contractIds);
        DefaultTableModel model = (DefaultTableModel) tblInvoice.getModel();
        model.setRowCount(0);
        DecimalFormat money = new DecimalFormat("#,##0.##");
        for (Invoice invoice : invoices) {
            model.addRow(new Object[]{invoice.getCode(), invoice.getMonth(), invoice.getYear(),
                    invoice.getDueDate(), money.format(invoice.getTotalAmount()), invoice.getStatus()});
        }
        lblStatus.setText("Số hóa đơn: " + invoices.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) btnSearchClick();
        else if (e.getSource() == btnInvoiceItem) btnInvoiceItemClick();
    }

    public void btnSearchClick() { loadInvoices(); }

    public void btnInvoiceItemClick() {
        int selectedRow = tblInvoice.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xem.");
            return;
        }
        int modelRow = tblInvoice.convertRowIndexToModel(selectedRow);
        Invoice selectedInvoice = invoices.get(modelRow);
        new InvoiceItemListFrm(user, selectedInvoice).setVisible(true);
        dispose();
    }
}
