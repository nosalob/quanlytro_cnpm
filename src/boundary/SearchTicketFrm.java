package boundary;

import dao.BuildingDAO;
import dao.IncidentTicketDAO;
import entity.Building;
import entity.IncidentTicket;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SearchTicketFrm extends JFrame implements ActionListener {
    private final User user;
    private final JComboBox<Building> cboBuilding = new JComboBox<>();
    private final JComboBox<String> cboStatus = new JComboBox<>(new String[]{"NEW", "IN_PROGRESS", "RESOLVED"});
    private final JButton btnSearch = new JButton("Tìm kiếm");
    private final JButton btnTicketItem = new JButton("Xem chi tiết");
    private final JButton btnBack = new JButton("Quay lại");
    private final JLabel lblStatus = new JLabel(" ");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Mã ticket", "Phòng", "Tiêu đề", "Trạng thái", "Ngày tạo"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblTicket = new JTable(tableModel);
    private List<IncidentTicket> tickets = new ArrayList<>();

    public SearchTicketFrm(User user) {
        this.user = user;
        setTitle("Tìm kiếm sự cố");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (Building b : new BuildingDAO().getAllBuildings()) cboBuilding.addItem(b);

        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Tòa nhà:"));
        top.add(cboBuilding);
        top.add(new JLabel("Trạng thái:"));
        top.add(cboStatus);
        top.add(btnSearch);

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(btnTicketItem);
        bottom.add(btnBack);
        bottom.add(lblStatus);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblTicket), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnSearch.addActionListener(this);
        btnTicketItem.addActionListener(this);
        btnBack.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) btnSearchClick();
        else if (e.getSource() == btnTicketItem) btnTicketItemClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    public void btnSearchClick() {
        Building b = (Building) cboBuilding.getSelectedItem();
        if (b == null) {
            lblStatus.setText("Chưa có tòa nhà");
            return;
        }
        tickets = new IncidentTicketDAO().searchTicket(b.getId(), cboStatus.getSelectedItem().toString());
        tableModel.setRowCount(0);
        for (IncidentTicket t : tickets) {
            tableModel.addRow(new Object[]{t.getCode(), t.getRoom().getCode(), t.getTitle(),
                    t.getStatus(), t.getCreatedAt()});
        }
        lblStatus.setText(tickets.isEmpty() ? "Không tìm thấy ticket" : "Tìm thấy " + tickets.size() + " ticket");
    }

    public void btnTicketItemClick() {
        int row = tblTicket.getSelectedRow();
        if (row < 0) {
            lblStatus.setText("Hãy chọn một ticket");
            return;
        }
        new TicketDetailFrm(user, tickets.get(row)).setVisible(true);
        dispose();
    }

    public void btnBackClick() {
        new LandlordHomeFrm(user).setVisible(true);
        dispose();
    }
}
