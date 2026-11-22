package boundary;

import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LandlordHomeFrm extends JFrame implements ActionListener {
    private final User user;
    private final JButton btnInvoiceList = new JButton("Danh sách hóa đơn");
    private final JButton btnLogout = new JButton("Đăng xuất");

    public LandlordHomeFrm(User user) {
        if (user == null || !"LANDLORD".equalsIgnoreCase(user.getRole()))
            throw new IllegalArgumentException("Cần tài khoản chủ trọ");
        this.user = user;
        setTitle("Trang chủ chủ trọ");
        setSize(550, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel welcome = new JLabel("Xin chào, " + user.getFullName(), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        root.add(welcome, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnInvoiceList);
        buttons.add(btnLogout);
        root.add(buttons, BorderLayout.SOUTH);
        btnInvoiceList.addActionListener(this);
        btnLogout.addActionListener(this);
        setContentPane(root);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnInvoiceList) {
            new InvoiceListFrm(user).setVisible(true);
            dispose();
        } else if (e.getSource() == btnLogout) {
            new LoginFrm().setVisible(true);
            dispose();
        }
    }
}
