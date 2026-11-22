package boundary;

import dao.InvoiceDAO;
import dao.PaymentProofDAO;
import entity.Invoice;
import entity.PaymentProof;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class SubmitPaymentProofFrm extends JFrame implements ActionListener {
    private Invoice invoice;
    private JButton btnChooseImage;
    private JButton btnSubmit;
    private JButton btnBack;
    private JLabel lblImage;
    private User user;
    private JTextField txtAmount;
    private JTextField txtTransferDate;
    private JTextField txtNote;
    private PaymentProof paymentProof;
    private File selectedImage;

    private static final String PROOF_STATUS_WAITING = "Chờ xác nhận";
    private static final String INVOICE_STATUS_WAITING = "Chờ xác nhận";

    public SubmitPaymentProofFrm(User user, Invoice invoice) {
        this.user = user;
        this.invoice = invoice;
        setTitle("Nộp minh chứng thanh toán");
        setSize(650, 570);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("NỘP MINH CHỨNG THANH TOÁN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Hóa đơn:"), gbc);
        gbc.gridx = 1;
        form.add(new JLabel(invoice.getCode() + " - tháng " + invoice.getMonth() + "/" + invoice.getYear()), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Số tiền đã chuyển:"), gbc);
        txtAmount = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(txtAmount, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Ngày chuyển (yyyy-MM-dd):"), gbc);
        txtTransferDate = new JTextField(LocalDate.now().toString());
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(txtTransferDate, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Ghi chú:"), gbc);
        txtNote = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(txtNote, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Ảnh minh chứng:"), gbc);
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        btnChooseImage = new JButton("Chọn ảnh");
        lblImage = new JLabel("Chưa chọn ảnh", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(300, 180));
        lblImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePanel.add(btnChooseImage, BorderLayout.NORTH);
        imagePanel.add(lblImage, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(imagePanel, gbc);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại");
        btnSubmit = new JButton("Gửi minh chứng");
        btnBack.addActionListener(this);
        btnChooseImage.addActionListener(this);
        btnSubmit.addActionListener(this);
        buttons.add(btnBack);
        buttons.add(btnSubmit);
        add(buttons, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnChooseImage) btnChooseImageClick();
        else if (e.getSource() == btnSubmit) btnSubmitClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    public void btnChooseImageClick() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn ảnh minh chứng chuyển khoản");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Ảnh (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String name = file.getName().toLowerCase(Locale.ROOT);
            if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ảnh JPG, JPEG hoặc PNG.");
                return;
            }
            selectedImage = file;
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(300, 180, Image.SCALE_SMOOTH);
            lblImage.setText("");
            lblImage.setIcon(new ImageIcon(scaled));
        }
    }

    public void btnSubmitClick() {
        if (user == null || invoice == null) {
            JOptionPane.showMessageDialog(this, "Thiếu thông tin người dùng hoặc hóa đơn.");
            return;
        }
        if (selectedImage == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ảnh minh chứng.");
            return;
        }

        double amount;
        Date transferDate;
        try {
            String amountText = txtAmount.getText().trim().replace(",", "");
            amount = Double.parseDouble(amountText);
            if (!Double.isFinite(amount) || amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền phải là số lớn hơn 0.");
            txtAmount.requestFocus();
            return;
        }
        try {
            transferDate = Date.valueOf(LocalDate.parse(txtTransferDate.getText().trim()));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày chuyển không hợp lệ. Nhập theo dạng yyyy-MM-dd.");
            txtTransferDate.requestFocus();
            return;
        }

        String savedImagePath;
        Path copiedImage = null;
        try {
            String originalName = selectedImage.getName();
            String extension = originalName.substring(originalName.lastIndexOf('.'));
            String newName = "proof_" + System.currentTimeMillis() + extension;
            Path directory = Path.of("images", "payment_proofs");
            Files.createDirectories(directory);
            copiedImage = directory.resolve(newName);
            Files.copy(selectedImage.toPath(), copiedImage, StandardCopyOption.REPLACE_EXISTING);
            savedImagePath = Path.of("images", "payment_proofs", newName).toString().replace('\\', '/');
            if (savedImagePath.length() > 255) throw new IllegalStateException("Đường dẫn ảnh vượt quá 255 ký tự.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể lưu ảnh minh chứng: " + ex.getMessage());
            return;
        }

        paymentProof = new PaymentProof();
        paymentProof.setImageUrl(savedImagePath);
        paymentProof.setDeclaredAmount(amount);
        paymentProof.setTransferDate(transferDate);
        paymentProof.setNote(txtNote.getText().trim().isEmpty() ? null : txtNote.getText().trim());
        paymentProof.setStatus(PROOF_STATUS_WAITING);
        paymentProof.setSubmittedAt(Date.valueOf(LocalDate.now()));

        boolean inserted = new PaymentProofDAO().addPaymentProof(paymentProof, invoice.getId(), user.getId());
        if (!inserted) {
            try {
                Files.deleteIfExists(copiedImage);
            } catch (Exception ignored) {
            }
            JOptionPane.showMessageDialog(this, "Không lưu được minh chứng. Vui lòng kiểm tra kết nối và dữ liệu.");
            return;
        }

        boolean updated = new InvoiceDAO().updateStatus(invoice.getId(), INVOICE_STATUS_WAITING);
        if (!updated) {
            JOptionPane.showMessageDialog(this,
                "Minh chứng đã được lưu nhưng chưa cập nhật được trạng thái hóa đơn. " +
                "Vui lòng báo chủ trọ kiểm tra hóa đơn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Nộp minh chứng thành công. Vui lòng chờ chủ trọ xác nhận.");
        new InvoiceListFrm(user).setVisible(true);
        dispose();
    }

    public void btnBackClick() {
        new InvoiceItemListFrm(user, invoice).setVisible(true);
        dispose();
    }
}
