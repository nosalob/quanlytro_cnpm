package boundary;

import dao.CompensationDAO;
import dao.ContractDAO;
import entity.Compensation;
import entity.Contract;
import entity.IncidentTicket;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateCompensationFrm extends JFrame implements ActionListener {
    private final User user;
    private final IncidentTicket ticket;
    private final Contract contract;
    private double depositBalance;
    private final JLabel lblTicketInfo = new JLabel();
    private final JLabel lblDepositBalance = new JLabel();
    private final JTextField txtAmount = new JTextField(15);
    private final JTextArea txtReason = new JTextArea(4, 20);
    private final JComboBox<String> cboMethod = new JComboBox<>(new String[]{"DEDUCT_DEPOSIT", "CASH", "BANK_TRANSFER"});
    private final JButton btnSave = new JButton("Lưu");
    private final JButton btnCancel = new JButton("Hủy");

    public CreateCompensationFrm(User user, IncidentTicket ticket) {
        this.user = user;
        this.ticket = ticket;
        setTitle("Tạo bồi thường");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTicketInfo.setText("Ticket " + ticket.getCode() + " - Phòng " + ticket.getRoom().getCode());

        // lấy hợp đồng đang hiệu lực và số dư cọc
        ContractDAO contractDAO = new ContractDAO();
        contract = contractDAO.getActiveContractByRoom(ticket.getRoom().getId());
        if (contract != null) {
            depositBalance = contractDAO.getDepositBalance(contract.getId());
            lblDepositBalance.setText("Hợp đồng " + contract.getCode() + " - Số dư cọc: "
                + String.format("%,.0f", depositBalance) + " VND");
        } else {
            lblDepositBalance.setText("Phòng này không có hợp đồng đang hiệu lực");
            btnSave.setEnabled(false);
        }

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(lblTicketInfo);
        top.add(lblDepositBalance);

        JPanel form = new JPanel(new GridLayout(3, 2));
        form.add(new JLabel("Số tiền (VND):"));
        form.add(txtAmount);
        form.add(new JLabel("Lý do:"));
        form.add(new JScrollPane(txtReason));
        form.add(new JLabel("Phương thức:"));
        form.add(cboMethod);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnSave);
        buttons.add(btnCancel);

        add(top, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        btnSave.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) btnSaveClick();
        else if (e.getSource() == btnCancel) btnCancelClick();
    }

    public void btnSaveClick() {
        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền phải là số");
            return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0");
            return;
        }
        String reason = txtReason.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do");
            return;
        }
        String method = cboMethod.getSelectedItem().toString();
        if (method.equals("DEDUCT_DEPOSIT") && amount > depositBalance) {
            JOptionPane.showMessageDialog(this, "Số tiền vượt quá số dư cọc");
            return;
        }

        Compensation comp = new Compensation();
        comp.setAmount(amount);
        comp.setReason(reason);
        comp.setMethod(method);
        comp.setStatus(method.equals("DEDUCT_DEPOSIT") ? "SETTLED" : "PENDING");

        if (new CompensationDAO().addCompensation(ticket.getId(), comp)) {
            JOptionPane.showMessageDialog(this, "Tạo bồi thường thành công");
            btnCancelClick();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo bồi thường thất bại");
        }
    }

    public void btnCancelClick() {
        new SearchTicketFrm(user).setVisible(true);
        dispose();
    }
}
