package boundary;

import entity.User;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ContractMenuFrm extends JFrame implements ActionListener {
    private User u;
    private JButton btnCreateContract, btnBack;

    public ContractMenuFrm(User u) {
        super("BHMS - Quản lý Hợp đồng");
        this.u = u;
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        btnCreateContract = new JButton("Tạo hợp đồng");
        btnCreateContract.addActionListener(this);
        add(btnCreateContract);

        JButton btnRenew = new JButton("Gia hạn hợp đồng");
        btnRenew.setEnabled(false);
        add(btnRenew);

        JButton btnChangeRep = new JButton("Đổi người đại diện");
        btnChangeRep.setEnabled(false);
        add(btnChangeRep);

        JButton btnTerminate = new JButton("Chấm dứt hợp đồng");
        btnTerminate.setEnabled(false);
        add(btnTerminate);

        btnBack = new JButton("Quay lại");
        btnBack.addActionListener(this);
        add(btnBack);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCreateContract) {
            btnCreateContractClick();
        } else if (e.getSource() == btnBack) {
            btnBackClick();
        }
    }

    private void btnCreateContractClick() {
        new SearchRoomFrm(u).setVisible(true);
        this.dispose();
    }

    private void btnBackClick() {
        new LandlordHomeFrm(u).setVisible(true);
        this.dispose();
    }
}
