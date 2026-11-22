package boundary;

import dao.IncidentTicketDAO;
import entity.IncidentTicket;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicketDetailFrm extends JFrame implements ActionListener {
    private final User user;
    private final IncidentTicket ticket;
    private final JLabel lblTicketInfo = new JLabel();
    private final JComboBox<String> cboStatus = new JComboBox<>(new String[]{"IN_PROGRESS", "RESOLVED"});
    private final JComboBox<String> cboFaultType = new JComboBox<>(new String[]{"TENANT_FAULT", "LANDLORD_FAULT", "OTHER"});
    private final JTextArea txtNote = new JTextArea(4, 20);
    private final JButton btnUpdate = new JButton("Cập nhật");
    private final JButton btnBack = new JButton("Quay lại");

    public TicketDetailFrm(User user, IncidentTicket selected) {
        this.user = user;
        // lấy lại chi tiết ticket từ DB
        IncidentTicket detail = new IncidentTicketDAO().getTicketDetail(selected.getId());
        this.ticket = detail != null ? detail : selected;

        setTitle("Chi tiết sự cố");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTicketInfo.setText("<html>Mã: " + ticket.getCode()
                + "<br>Phòng: " + ticket.getRoom().getCode()
                + "<br>Người thuê: " + ticket.getUser().getFullName() + " - " + ticket.getUser().getPhone()
                + "<br>Tiêu đề: " + ticket.getTitle()
                + "<br>Mô tả: " + ticket.getDescription()
                + "<br>Ngày tạo: " + ticket.getCreatedAt()
                + "<br>Trạng thái hiện tại: " + ticket.getStatus() + "</html>");
        cboFaultType.setEnabled(false);

        JPanel form = new JPanel(new GridLayout(3, 2));
        form.add(new JLabel("Trạng thái mới:"));
        form.add(cboStatus);
        form.add(new JLabel("Phân loại lỗi:"));
        form.add(cboFaultType);
        form.add(new JLabel("Ghi chú:"));
        form.add(new JScrollPane(txtNote));

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnUpdate);
        buttons.add(btnBack);

        add(lblTicketInfo, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        cboStatus.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnBack.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cboStatus) {
            // chỉ chọn phân loại lỗi khi RESOLVED
            cboFaultType.setEnabled("RESOLVED".equals(cboStatus.getSelectedItem()));
        } else if (e.getSource() == btnUpdate) btnUpdateClick();
        else if (e.getSource() == btnBack) btnBackClick();
    }

    public void btnUpdateClick() {
        String status = cboStatus.getSelectedItem().toString();
        String note = txtNote.getText().trim();
        String faultType = status.equals("RESOLVED") ? cboFaultType.getSelectedItem().toString() : null;

        if (note.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ghi chú");
            return;
        }

        ticket.setStatus(status);
        ticket.setFaultType(faultType);
        ticket.setResolutionNote(note);

        if (!new IncidentTicketDAO().updateStatus(ticket.getId(), status, faultType, note)) {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại");
            return;
        }

        if ("TENANT_FAULT".equals(faultType)) {
            new CreateCompensationFrm(user, ticket).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công");
            btnBackClick();
        }
    }

    public void btnBackClick() {
        new SearchTicketFrm(user).setVisible(true);
        dispose();
    }
}
