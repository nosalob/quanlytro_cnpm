package boundary;

import dao.InvoiceDAO;
import dao.InvoiceItemDAO;
import dao.PaymentProofDAO;
import entity.Invoice;
import entity.InvoiceItem;
import entity.PaymentProof;
import entity.User;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class ReviewPaymentProofFrm extends JFrame implements ActionListener {
    private final User user;
    private final Invoice invoice;
    private PaymentProof proof;
    private JTable tblInvoiceItem;
    private JLabel lblProofImage, lblStatus;
    private JTextArea pnlInfor;
    private final JButton btnConfirm = new JButton("Xác nhận thanh toán");
    private final JButton btnReject = new JButton("Từ chối minh chứng");
    private final JButton btnBack = new JButton("Quay lại");

    public ReviewPaymentProofFrm(User user, Invoice invoice) {
        if (user == null || !"LANDLORD".equalsIgnoreCase(user.getRole()))
        throw new IllegalArgumentException("Chỉ chủ trọ được duyệt minh chứng");
        this.user = user;
        this.invoice = invoice;
        setTitle("Duyệt minh chứng - " + invoice.getCode());
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        lblStatus = new JLabel("Hóa đơn " + invoice.getCode() + " - " + invoice.getStatus());
        lblStatus.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblStatus, BorderLayout.NORTH);
        tblInvoiceItem = new JTable(new DefaultTableModel(
                new String[]{"Loại", "Mô tả", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
        });
        JPanel right = new JPanel(new BorderLayout(8, 8));
        pnlInfor = new JTextArea(7, 34);
        pnlInfor.setEditable(false);
        pnlInfor.setLineWrap(true);
        pnlInfor.setWrapStyleWord(true);
        right.add(new JScrollPane(pnlInfor), BorderLayout.NORTH);
        lblProofImage = new JLabel("Chưa có hình ảnh minh chứng", SwingConstants.CENTER);
        lblProofImage.setPreferredSize(new Dimension(380, 320));
        lblProofImage.setBorder(BorderFactory.createTitledBorder("Ảnh minh chứng"));
        right.add(new JScrollPane(lblProofImage), BorderLayout.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(tblInvoiceItem), right);
        split.setResizeWeight(0.52);
        add(split, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton b : new JButton[]{btnBack, btnReject, btnConfirm}) {
            b.addActionListener(this);
            bottom.add(b);
        }
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            proof = new PaymentProofDAO().getProofByInvoice(invoice.getId());
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            proof = null;
        }
        List<InvoiceItem> items = new InvoiceItemDAO().getInvoiceItem(invoice.getId());
        DefaultTableModel m = (DefaultTableModel) tblInvoiceItem.getModel();
        m.setRowCount(0);
        DecimalFormat money = new DecimalFormat("#,##0.##");
        for (InvoiceItem item : items)
        m.addRow(new Object[]{item.getItemType(), item.getDescription(),
                item.getQuantity(), money.format(item.getUnitPrice()), money.format(item.getAmount())});
        if (proof == null) {
            pnlInfor.setText("Hóa đơn chưa có minh chứng thanh toán.");
            lblProofImage.setIcon(null);
            lblProofImage.setText("Chưa có hình ảnh minh chứng");
        } else {
            pnlInfor.setText("Mã minh chứng: " + proof.getId() +
                "\nSố tiền khai báo: " + money.format(proof.getDeclaredAmount()) +
                "\nNgày chuyển: " + proof.getTransferDate() +
                "\nNgày nộp: " + proof.getSubmittedAt() +
                "\nTrạng thái: " + proof.getStatus() +
                "\nGhi chú: " + proof.getNote() +
                "\nLý do từ chối: " + proof.getRejectReason());
            showImage(proof.getImageUrl());
        }
        boolean pending = proof != null && "Chờ xác nhận".equals(proof.getStatus());
        btnConfirm.setEnabled(pending);
        btnReject.setEnabled(pending);
    }

    private void showImage(String location) {
        lblProofImage.setIcon(null);
        if (location == null || location.isBlank()) {
            lblProofImage.setText("Không có đường dẫn ảnh");
            return;
        }
        try {
            Path file = Path.of(location);
            if (!Files.isRegularFile(file)) {
                lblProofImage.setText("Không tìm thấy ảnh: " + location);
                return;
            }
            ImageIcon original = new ImageIcon(file.toAbsolutePath().toString());
            if (original.getIconWidth() <= 0) {
                lblProofImage.setText("Không đọc được ảnh: " + location);
                return;
            }
            double scale = Math.min(380.0 / original.getIconWidth(), 310.0 / original.getIconHeight());
            Image scaled = original.getImage().getScaledInstance(
                Math.max(1, (int) (original.getIconWidth() * scale)),
                Math.max(1, (int) (original.getIconHeight() * scale)), Image.SCALE_SMOOTH);
            lblProofImage.setText("");
            lblProofImage.setIcon(new ImageIcon(scaled));
        } catch (RuntimeException ex) {
            lblProofImage.setText("Đường dẫn ảnh không hợp lệ");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) btnConfirmClick();
        else if (e.getSource() == btnReject) btnRejectClick();
        else if (e.getSource() == btnBack) back();
    }

    private void btnConfirmClick() {
        if (JOptionPane.showConfirmDialog(this, "Xác nhận hóa đơn đã thanh toán?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        review(true, null);
    }

    private void btnRejectClick() {
        String reason = JOptionPane.showInputDialog(this, "Nhập lý do từ chối:");
        if (reason == null) return;
        if (reason.isBlank() || reason.length() > 255) {
            JOptionPane.showMessageDialog(this, "Lý do phải có nội dung và không quá 255 ký tự.");
            return;
        }
        review(false, reason);
    }

    private void review(boolean approve, String reason) {
        if (proof == null) return;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                PaymentProofDAO proofs = new PaymentProofDAO(con);
                InvoiceDAO invoices = new InvoiceDAO(con);
                boolean updatedProof = approve ? proofs.confirm(proof.getId(), user.getId())
                : proofs.reject(proof.getId(), user.getId(), reason);
                boolean updatedInvoice = updatedProof && invoices.updateStatus(invoice.getId(),
                    approve ? "Đã thanh toán" : "Chua thanh toan");
                if (!updatedProof || !updatedInvoice) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this,
                        "Không duyệt được. Minh chứng có thể đã được xử lý hoặc dữ liệu không hợp lệ.");
                    loadData();
                    return;
                }
                con.commit();
                JOptionPane.showMessageDialog(this, approve ? "Đã xác nhận thanh toán." : "Đã từ chối minh chứng.");
                back();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không lưu được kết quả duyệt: " + ex.getMessage());
        }
    }

    private void back() {
        new SearchPendingPaymentFrm(user).setVisible(true);
        dispose();
    }
}
