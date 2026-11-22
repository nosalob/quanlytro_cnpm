package boundary;

import dao.BuildingDAO;
import dao.RoomDAO;
import entity.Building;
import entity.Room;
import entity.User;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SearchRoomFrm extends JFrame implements ActionListener {
    private User u;
    private ArrayList<Room> rooms = new ArrayList<>();
    private JComboBox<Building> cbBuilding;
    private JTextField txtKeyword;
    private JButton btnSearch, btnNext, btnBack;
    private JTable tblResult;
    private DefaultTableModel tableModel;

    public SearchRoomFrm(User u) {
        super("Create Contract - Bước 1: Chọn phòng trống");
        this.u = u;
        setSize(700, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(new JLabel("Tòa nhà:"));
        cbBuilding = new JComboBox<>();
        loadBuildings();
        pnlTop.add(cbBuilding);

        pnlTop.add(new JLabel("Mã phòng:"));
        txtKeyword = new JTextField(12);
        pnlTop.add(txtKeyword);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pnlTop.add(btnSearch);
        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"ID", "Mã phòng", "Diện tích (m²)", "Giá thuê", "Tiền cọc", "Số người tối đa"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblResult = new JTable(tableModel);
        tblResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tblResult), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Back");
        btnBack.addActionListener(this);
        btnNext = new JButton("Next");
        btnNext.addActionListener(this);
        pnlBottom.add(btnBack);
        pnlBottom.add(btnNext);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadBuildings() {
        try (BuildingDAO bd = new BuildingDAO()) {
            for (Building b : bd.getActiveBuildings()) {
                cbBuilding.addItem(b);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) btnSearchClick();
        else if (e.getSource() == btnNext) btnNextClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    private void btnSearchClick() {
        Building selectedBuilding = (Building) cbBuilding.getSelectedItem();
        if (selectedBuilding == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tòa nhà!");
            return;
        }
        tableModel.setRowCount(0);
        try (RoomDAO rd = new RoomDAO()) {
            rooms = rd.searchVacantRoom(selectedBuilding.getId(), txtKeyword.getText().trim());
        }
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng trống phù hợp.");
            return;
        }
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getCode(), r.getArea(), r.getRentPrice(), r.getDepositAmount(), r.getMaxOccupants()
            });
        }
    }

    private void btnNextClick() {
        int row = tblResult.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng trên bảng!");
            return;
        }
        Building selectedBuilding = (Building) cbBuilding.getSelectedItem();
        Room selectedRoom = rooms.get(row);
        new ContractInfoFrm(u, selectedBuilding, selectedRoom).setVisible(true);
        this.dispose();
    }

    private void btnBackClick() {
        new ContractMenuFrm(u).setVisible(true);
        this.dispose();
    }
}
