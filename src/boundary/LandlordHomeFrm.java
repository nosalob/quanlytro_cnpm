package boundary;

import entity.User;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LandlordHomeFrm extends JFrame implements ActionListener {
    private final User user;
    private final JButton btnManageContract = new JButton("Quản lý hợp đồng");
    private final JButton btnInvoiceList = new JButton("Danh sách hóa đơn");
<<<<<<< HEAD
    private final JButton btnIncidentTicket = new JButton("Báo cáo sự cố");
    private final JButton btnVerifyMeterReading = new JButton("Duyệt chỉ số điện nước");
=======
    private final JButton btnIncidentTicket = new JButton("Sự cố");
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
    private final JButton btnLogout = new JButton("Đăng xuất");

    public LandlordHomeFrm(User user) {
        if (user == null || !"LANDLORD".equalsIgnoreCase(user.getRole()))
            throw new IllegalArgumentException("Cần tài khoản chủ trọ");
        this.user = user;
        setTitle("Trang chủ - Chủ trọ");
        setSize(560, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel welcome = new JLabel("Xin chào, " + user.getFullName(), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
<<<<<<< HEAD
        root.add(welcome, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        JButton[] menuButtons = {
                btnManageContract,
                btnInvoiceList,
                btnIncidentTicket,
                btnVerifyMeterReading,
                btnLogout
        };
        for (JButton button : menuButtons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(340, 42));
            button.setMaximumSize(new Dimension(340, 42));
            buttons.add(button);
            buttons.add(Box.createVerticalStrut(12));
        }
        root.add(buttons, BorderLayout.CENTER);
        btnManageContract.addActionListener(this);
        btnInvoiceList.addActionListener(this);
        btnIncidentTicket.addActionListener(this);
        btnVerifyMeterReading.addActionListener(this);
=======
        root.add(welcome, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnInvoiceList);
        buttons.add(btnIncidentTicket);
        buttons.add(btnLogout);
        root.add(buttons, BorderLayout.SOUTH);
        btnInvoiceList.addActionListener(this);
        btnIncidentTicket.addActionListener(this);
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
        btnLogout.addActionListener(this);
        setContentPane(root);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnManageContract) {
            new ContractMenuFrm(user).setVisible(true);
            dispose();
        } else if (e.getSource() == btnInvoiceList) {
            new InvoiceListFrm(user).setVisible(true);
            dispose();
        } else if (e.getSource() == btnIncidentTicket) {
            new SearchTicketFrm(user).setVisible(true);
            dispose();
<<<<<<< HEAD
        } else if (e.getSource() == btnVerifyMeterReading) {
            new SearchMeterReadingFrm(user).setVisible(true);
            dispose();
=======
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
        } else if (e.getSource() == btnLogout) {
            new LoginFrm().setVisible(true);
            dispose();
        }
    }
}
