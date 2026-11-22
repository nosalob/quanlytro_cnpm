package boundary;

import dao.InvoiceItemDAO;
import entity.Invoice;
import entity.InvoiceItem;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class InvoiceItemListFrm extends JFrame implements ActionListener {
    private Invoice invoice;
    private JButton btnSubmitProof;
    private JButton btnBack;
    private JTable tblInvoiceItem;
    private JLabel lblStatus;
    private JLabel lblMonth;
    private User user;

    public InvoiceItemListFrm(User user, Invoice invoice) {
        this.user = user;
        this.invoice = invoice;
        setTitle("Chi tiết hóa đơn " + invoice.getCode());
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadInvoiceItems();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel info = new JPanel(new GridLayout(2, 1, 4, 4));
        lblMonth = new JLabel("Hóa đơn tháng " + invoice.getMonth() + "/" + invoice.getYear()
            + " - Mã: " + invoice.getCode());
        lblStatus = new JLabel("Trạng thái: " + invoice.getStatus());
        info.add(lblMonth);
        info.add(lblStatus);
        add(info, BorderLayout.NORTH);

        String[] columns = {"Loại khoản thu", "Mô tả", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInvoiceItem = new JTable(model);
        add(new JScrollPane(tblInvoiceItem), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại");
        btnSubmitProof = new JButton("Nộp minh chứng");
        btnBack.addActionListener(this);
        btnSubmitProof.addActionListener(this);
        buttons.add(btnBack);
        buttons.add(btnSubmitProof);
        add(buttons, BorderLayout.SOUTH);
    }

    private void loadInvoiceItems() {
        List<InvoiceItem> items = new InvoiceItemDAO().getInvoiceItem(invoice.getId());
        DefaultTableModel model = (DefaultTableModel) tblInvoiceItem.getModel();
        model.setRowCount(0);
        DecimalFormat number = new DecimalFormat("#,##0.##");
        for (InvoiceItem item : items) {
            model.addRow(new Object[]{item.getItemType(), item.getDescription(),
                    number.format(item.getQuantity()), number.format(item.getUnitPrice()),
                    number.format(item.getAmount())});
        }
        lblStatus.setText("Trạng thái: " + invoice.getStatus() + " | Số khoản thu: " + items.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmitProof) btnInvoiceItemClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    public void btnInvoiceItemClick() {
        new SubmitPaymentProofFrm(user, invoice).setVisible(true);
        dispose();
    }

    public void btnBackClick() {
        new InvoiceListFrm(user).setVisible(true);
        dispose();
    }
}
