package boundary;

import dao.ContractDAO;
import dao.UserDAO;
import entity.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ContractConfirmFrm extends JFrame implements ActionListener {
    private User u;
    private Building building;
    private Room room;
    private Contract contract;
    private Resident representative;

    private JTextArea txtSummary;
    private JButton btnConfirm, btnBack;

    public ContractConfirmFrm(User u, Building building, Room room, Contract contract) {
        super("Tạo hợp đồng - Bước 5: Xác nhận và hoàn tất");
        this.u = u;
        this.building = building;
        this.room = room;
        this.contract = contract;

        setSize(540, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        for (Occupancy o : contract.getOccupancies()) {
            if (o.isRepresentative()) {
                this.representative = o.getResident();
            }
        }
        try (UserDAO ud = new UserDAO()) {
            ud.prepareTenantAccount(building.getCode(), room, representative);
        }

        txtSummary = new JTextArea();
        txtSummary.setEditable(false);
        txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtSummary.setText(buildSummaryText());
        add(new JScrollPane(txtSummary), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại"); btnBack.addActionListener(this);
        btnConfirm = new JButton("Xác nhận"); btnConfirm.addActionListener(this);
        pnlBottom.add(btnBack);
        pnlBottom.add(btnConfirm);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private String buildSummaryText() {
        ContractTerm term = contract.getTerms().get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("=== THÔNG TIN HỢP ĐỒNG MỚI ===\n\n");
        sb.append("Tòa nhà        : ").append(building.getName()).append("\n");
        sb.append("Phòng thuê     : ").append(room.getCode()).append("\n");
        sb.append("Thời hạn       : ").append(term.getStartDate()).append(" -> ").append(term.getEndDate()).append("\n");
        sb.append("Giá thuê       : ").append(term.getRentPrice()).append(" VND/tháng\n");
        sb.append("Tiền cọc       : ").append(contract.getDepositAmount()).append(" VND\n");
        sb.append("Người đại diện : ").append(representative.getFullName()).append(" (CCCD: ").append(representative.getIdCardNo()).append(")\n");
        sb.append("Tổng số người  : ").append(contract.getOccupancies().size()).append(" người\n\n");
        sb.append("=== TÀI KHOẢN TENANT SẼ CẤP ===\n");
        sb.append("Username       : ").append(representative.getUsername()).append("\n");
        sb.append("Mật khẩu tạm   : ").append(representative.getTempPassword()).append("\n");
        return sb.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) btnConfirmClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    private void btnConfirmClick() {
        try (ContractDAO cd = new ContractDAO()) {
            if (cd.addContract(contract, room)) {
                String msg = "Tạo hợp đồng thành công!\n"
                + "Mã hợp đồng: " + contract.getCode() + "\n"
                + "Tài khoản Tenant: " + representative.getUsername() + "\n"
                + "Mật khẩu tạm thời: " + representative.getTempPassword() + "\n"
                + "(Vui lòng bàn giao thông tin này cho khách thuê)";
                JOptionPane.showMessageDialog(this, msg, "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                new LandlordHomeFrm(u).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu hợp đồng thất bại (phòng có thể vừa được thuê)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnBackClick() {
        new OccupantsFrm(u, building, room, contract).setVisible(true);
        this.dispose();
    }
}
