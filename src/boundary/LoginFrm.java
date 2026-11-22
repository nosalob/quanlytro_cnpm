package boundary;

import dao.UserDAO;
import entity.User;
import util.DataAccessException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrm extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrm() {
        setTitle("Đăng nhập - Quản lý trọ");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        main.add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 12));
        form.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        form.add(txtUsername);
        form.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        form.add(txtPassword);
        main.add(form, BorderLayout.CENTER);
        btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(this);
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnLogin);
        main.add(buttons, BorderLayout.SOUTH);
        setContentPane(main);
        getRootPane().setDefaultButton(btnLogin);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != btnLogin) return;
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
            return;
        }
        User input = new User();
        input.setUsername(username);
        input.setPasswordHash(password);
        User user;
        try (UserDAO userDAO = new UserDAO()) {
            user = userDAO.checkLogin(input);
        } catch (DataAccessException ex) {
            Throwable cause = ex.getCause();
            String detail = cause == null ? ex.getMessage() : cause.getMessage();
            JOptionPane.showMessageDialog(this, "Không thể truy cập cơ sở dữ liệu: " + detail,
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thất bại hoặc tài khoản không hoạt động.");
            txtPassword.setText("");
            return;
        }
        if ("LANDLORD".equalsIgnoreCase(user.getRole())) new LandlordHomeFrm(user).setVisible(true);
        else if ("TENANT".equalsIgnoreCase(user.getRole())) new TenantHomeFrm(user).setVisible(true);
        else {
            JOptionPane.showMessageDialog(this, "Vai trò chưa được hỗ trợ.");
            return;
        }
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrm().setVisible(true));
    }
}
