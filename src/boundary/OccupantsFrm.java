package boundary;

import dao.ResidentDAO;
import dao.RoomDAO;
import entity.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.ValidationUtil;

public class OccupantsFrm extends JFrame implements ActionListener {
    private User u;
    private Building building;
    private Room room;
    private Contract contract;

    private JLabel lblCount;
    private JTable tblOccupant;
    private DefaultTableModel tableModel;
    private JTextField txtFullName, txtIdCardNo, txtPhone, txtDob, txtHometown;
    private JButton btnAdd, btnRemove, btnNext, btnBack;

    public OccupantsFrm(User u, Building building, Room room, Contract contract) {
        super("Tạo hợp đồng - Bước 4: Danh sách người ở cùng");
        this.u = u;
        this.building = building;
        this.room = room;
        this.contract = contract;

        setSize(720, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        lblCount = new JLabel();
        lblCount.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        add(lblCount, BorderLayout.NORTH);

        String[] cols = {"Họ tên", "CCCD", "Số điện thoại", "Vai trò"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblOccupant = new JTable(tableModel);
        add(new JScrollPane(tblOccupant), BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 6, 6));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Thêm người ở cùng"));
        pnlForm.add(new JLabel("Họ tên (*):")); txtFullName = new JTextField(); pnlForm.add(txtFullName);
        pnlForm.add(new JLabel("CCCD (*):"));   txtIdCardNo = new JTextField(); pnlForm.add(txtIdCardNo);
        pnlForm.add(new JLabel("SĐT:"));        txtPhone = new JTextField();    pnlForm.add(txtPhone);
        pnlForm.add(new JLabel("Ngày sinh:"));  txtDob = new JTextField("2000-01-01"); pnlForm.add(txtDob);
        pnlForm.add(new JLabel("Quê quán:"));   txtHometown = new JTextField("Hà Nội"); pnlForm.add(txtHometown);

        btnAdd = new JButton("Thêm"); btnAdd.addActionListener(this);
        btnRemove = new JButton("Xóa người đã chọn"); btnRemove.addActionListener(this);
        pnlForm.add(btnAdd); pnlForm.add(btnRemove);
        pnlSouth.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại"); btnBack.addActionListener(this);
        btnNext = new JButton("Tiếp theo"); btnNext.addActionListener(this);
        pnlNav.add(btnBack); pnlNav.add(btnNext);
        pnlSouth.add(pnlNav, BorderLayout.SOUTH);

        add(pnlSouth, BorderLayout.SOUTH);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Occupancy o : contract.getOccupancies()) {
            Resident r = o.getResident();
            tableModel.addRow(new Object[]{
                    r.getFullName(), r.getIdCardNo(), r.getPhone(),
                    o.isRepresentative() ? "Người đại diện" : "Ở cùng"
            });
        }
        lblCount.setText("Số người hiện tại: " + contract.getOccupancies().size() + " / " + room.getMaxOccupants());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) btnAddClick();
        else if (e.getSource() == btnRemove) btnRemoveClick();
        else if (e.getSource() == btnNext) btnNextClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    private void btnAddClick() {
        String name = txtFullName.getText().trim();
        String cccd = txtIdCardNo.getText().trim();
        if (name.isEmpty() || cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên và số CCCD!");
            return;
        }
        for (Occupancy o : contract.getOccupancies()) {
            if (o.getResident().getIdCardNo().equals(cccd)) {
                JOptionPane.showMessageDialog(this, "Người này đã có trong danh sách phòng!");
                return;
            }
        }
        try (ResidentDAO rd = new ResidentDAO(); RoomDAO roomDao = new RoomDAO()) {
            if (!roomDao.checkCapacity(room.getId(), contract.getOccupancies().size() + 1)) {
                JOptionPane.showMessageDialog(this, "Đã vượt quá số người tối đa của phòng (" + room.getMaxOccupants() + ")!");
                return;
            }
            List<Resident> found = rd.searchResident(cccd);
            Resident target = found.isEmpty() ? null : found.get(0);
            if (target == null) {
                LocalDate dob = ValidationUtil.parseDate(txtDob.getText());
                target = new Resident();
                target.setFullName(name);
                target.setIdCardNo(cccd);
                target.setPhone(txtPhone.getText().trim());
                target.setDateOfBirth(dob != null ? dob : LocalDate.of(2000, 1, 1));
                target.setHometown(txtHometown.getText().trim().isEmpty() ? "Chưa rõ" : txtHometown.getText().trim());
                if (!rd.addResident(target)) {
                    JOptionPane.showMessageDialog(this, "Không lưu được hồ sơ người ở!");
                    return;
                }
            }
            Occupancy occ = new Occupancy();
            occ.setRepresentative(false);
            occ.setResident(target);
            occ.setMoveInDate(contract.getTerms().get(0).getStartDate());
            contract.getOccupancies().add(occ);

            txtFullName.setText(""); txtIdCardNo.setText(""); txtPhone.setText("");
            refreshTable();
        }
    }

    private void btnRemoveClick() {
        int row = tblOccupant.getSelectedRow();
        if (row < 0) return;
        Occupancy target = contract.getOccupancies().get(row);
        if (target.isRepresentative()) {
            JOptionPane.showMessageDialog(this, "Không thể xóa người đại diện hợp đồng!");
            return;
        }
        contract.getOccupancies().remove(row);
        refreshTable();
    }

    private void btnNextClick() {
        new ContractConfirmFrm(u, building, room, contract).setVisible(true);
        this.dispose();
    }

    private void btnBackClick() {
        new RepresentativeFrm(u, building, room, contract).setVisible(true);
        this.dispose();
    }
}
