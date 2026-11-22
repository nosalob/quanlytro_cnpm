package boundary;

import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TenantHomeFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnInvoiceList;
    private JButton btnLogout;

    public TenantHomeFrm(User user) {
        this.user = user;
        setTitle("Trang chủ người thuê");
        setSize(560, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        String fullName = user != null && user.getFullName() != null
        ? user.getFullName() : "Người thuê";
        JLabel lblWelcome = new JLabel("Xin chào, " + fullName, SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 21));
        mainPanel.add(lblWelcome, BorderLayout.NORTH);

        JLabel lblDescription = new JLabel(
            "Quản lý hóa đơn và minh chứng thanh toán của bạn.",
            SwingConstants.CENTER);
        lblDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(lblDescription, BorderLayout.CENTER);

        btnInvoiceList = new JButton("Xem danh sách hóa đơn");
        btnLogout = new JButton("Đăng xuất");
        btnInvoiceList.addActionListener(this);
        btnLogout.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttonPanel.add(btnInvoiceList);
        buttonPanel.add(btnLogout);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnInvoiceList) {
            btnInvoiceListClick();
        } else if (e.getSource() == btnLogout) {
            btnLogoutClick();
        }
    }

    public void btnInvoiceListClick() {
        new InvoiceListFrm(user).setVisible(true);
        dispose();
    }

    private void btnLogoutClick() {
        new LoginFrm().setVisible(true);
        dispose();
    }
}
