import boundary.LoginFrm;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrm loginFrm = new LoginFrm();
            loginFrm.setVisible(true);
        });
    }
}