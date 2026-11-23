package boundary;

import dao.ResidentDAO;
import entity.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RepresentativeFrm extends JFrame implements ActionListener {
    private User u;
    private Building building;
    private Room room;
    private Contract contract;
    private ArrayList<Resident> residents = new ArrayList<>();

    private JTextField txtIdCardNo;
    private JButton btnSearch, btnSelect, btnAddResident, btnBack;
    private JTable tblResult;
    private DefaultTableModel tableModel;

    public RepresentativeFrm(User u, Building building, Room room, Contract contract) {
        super("Tạo hợp đồng - Bước 3: Chọn người đại diện");
        this.u = u;
        this.building = building;
        this.room = room;
        this.contract = contract;

        setSize(680, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(new JLabel("Số CCCD:"));
        txtIdCardNo = new JTextField(15);
        pnlTop.add(txtIdCardNo);
        btnSearch = new JButton("Tìm kiếm"); btnSearch.addActionListener(this);
        btnAddResident = new JButton("Thêm người thuê mới"); btnAddResident.addActionListener(this);
        pnlTop.add(btnSearch);
        pnlTop.add(btnAddResident);
        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Họ tên", "CCCD", "SĐT", "Ngày sinh", "Quê quán"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblResult = new JTable(tableModel);
        add(new JScrollPane(tblResult), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại"); btnBack.addActionListener(this);
        btnSelect = new JButton("Chọn người đại diện"); btnSelect.addActionListener(this);
        pnlBottom.add(btnBack);
        pnlBottom.add(btnSelect);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) btnSearchClick();
        else if (e.getSource() == btnSelect) btnSelectClick();
        else if (e.getSource() == btnAddResident) btnAddResidentClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    private void btnSearchClick() {
        String cccd = txtIdCardNo.getText().trim();
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số CCCD!");
            return;
        }
        tableModel.setRowCount(0);
        try (ResidentDAO rd = new ResidentDAO()) {
            residents = rd.searchResident(cccd);
        }
        if (residents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ. Hãy nhấn 'Thêm người thuê mới' để tạo mới.");
            return;
        }
        for (Resident r : residents) {
            tableModel.addRow(new Object[]{r.getFullName(), r.getIdCardNo(), r.getPhone(), r.getDateOfBirth(), r.getHometown()});
        }
    }

    private void btnSelectClick() {
        int row = tblResult.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người trên bảng làm đại diện!");
            return;
        }
        selectResidentAndProceed(residents.get(row));
    }

    private void btnAddResidentClick() {
        new AddResidentFrm(this).setVisible(true);
    }

    public void onResidentAdded(Resident newResident) {
        selectResidentAndProceed(newResident);
    }

    private void selectResidentAndProceed(Resident rep) {
        contract.getOccupancies().clear();
        Occupancy occ = new Occupancy();
        occ.setRepresentative(true);
        occ.setResident(rep);
        occ.setMoveInDate(contract.getTerms().get(0).getStartDate());
        contract.getOccupancies().add(occ);

        new OccupantsFrm(u, building, room, contract).setVisible(true);
        this.dispose();
    }

    private void btnBackClick() {
        new ContractInfoFrm(u, building, room).setVisible(true);
        this.dispose();
    }
}
