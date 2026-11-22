package boundary;

import dao.RoomDAO;
import entity.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import util.ValidationUtil;

public class ContractInfoFrm extends JFrame implements ActionListener {
    private User u;
    private Building building;
    private Room room;
    private JTextField txtStartDate, txtEndDate, txtRentPrice, txtDeposit;
    private JButton btnNext, btnBack;

    public ContractInfoFrm(User u, Building building, Room room) {
        super("Create Contract - Bước 2: Thông tin hợp đồng");
        this.u = u;
        this.building = building;
        this.room = room;

        setSize(480, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Tòa / Phòng:"), gbc);
        gbc.gridx = 1; add(new JLabel(building.getName() + " - Phòng " + room.getCode() + " (Tối đa " + room.getMaxOccupants() + " người)"), gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        txtStartDate = new JTextField(LocalDate.now().toString(), 15);
        gbc.gridx = 1; add(txtStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        txtEndDate = new JTextField(LocalDate.now().plusYears(1).minusDays(1).toString(), 15);
        gbc.gridx = 1; add(txtEndDate, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Giá thuê (VND):"), gbc);
        txtRentPrice = new JTextField(String.valueOf(room.getRentPrice()), 15);
        gbc.gridx = 1; add(txtRentPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Tiền cọc (VND):"), gbc);
        txtDeposit = new JTextField(String.valueOf(room.getDepositAmount()), 15);
        gbc.gridx = 1; add(txtDeposit, gbc);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Back"); btnBack.addActionListener(this);
        btnNext = new JButton("Next"); btnNext.addActionListener(this);
        pnlBtn.add(btnBack); pnlBtn.add(btnNext);
        gbc.gridx = 1; gbc.gridy = 5; add(pnlBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnNext) btnNextClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    private void btnNextClick() {
        LocalDate start = ValidationUtil.parseDate(txtStartDate.getText());
        LocalDate end = ValidationUtil.parseDate(txtEndDate.getText());
        Float rent = ValidationUtil.parseFloat(txtRentPrice.getText());
        Float deposit = ValidationUtil.parseFloat(txtDeposit.getText());

        if (start == null || end == null || rent == null || deposit == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ và đúng định dạng (Ngày yyyy-MM-dd, số tiền >= 0)!");
            return;
        }
        try (RoomDAO rd = new RoomDAO()) {
            if (!rd.checkAvailable(room.getId(), start, end)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu và phòng không được trùng hợp đồng khác!");
                return;
            }
        }
        Contract draft = new Contract();
        draft.setDepositAmount(deposit);
        ContractTerm term = new ContractTerm();
        term.setTermNo(1);
        term.setStartDate(start);
        term.setEndDate(end);
        term.setRentPrice(rent);
        term.setStatus("ACTIVE");
        draft.getTerms().add(term);

        new RepresentativeFrm(u, building, room, draft).setVisible(true);
        this.dispose();
    }

    private void btnBackClick() {
        new SearchRoomFrm(u).setVisible(true);
        this.dispose();
    }
}
