package boundary;

import dao.ResidentDAO;
import entity.Resident;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import util.ValidationUtil;

public class AddResidentFrm extends JFrame implements ActionListener {
    private RepresentativeFrm parent;
    private JTextField txtFullName, txtIdCardNo, txtPhone, txtDob, txtHometown, txtNote;
    private JButton btnSave, btnCancel;

    public AddResidentFrm(RepresentativeFrm parent) {
        super("Thêm hồ sơ khách thuê mới");
        this.parent = parent;
        setSize(440, 340);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Họ tên (*):"), gbc);
        txtFullName = new JTextField(18); gbc.gridx = 1; add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Số CCCD (*):"), gbc);
        txtIdCardNo = new JTextField(18); gbc.gridx = 1; add(txtIdCardNo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Số điện thoại:"), gbc);
        txtPhone = new JTextField(18); gbc.gridx = 1; add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Ngày sinh (yyyy-MM-dd) (*):"), gbc);
        txtDob = new JTextField("2000-01-01", 18); gbc.gridx = 1; add(txtDob, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Quê quán (*):"), gbc);
        txtHometown = new JTextField(18); gbc.gridx = 1; add(txtHometown, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(new JLabel("Ghi chú:"), gbc);
        txtNote = new JTextField(18); gbc.gridx = 1; add(txtNote, gbc);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu"); btnSave.addActionListener(this);
        btnCancel = new JButton("Hủy"); btnCancel.addActionListener(this);
        pnlBtn.add(btnSave); pnlBtn.add(btnCancel);
        gbc.gridx = 1; gbc.gridy = 6; add(pnlBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) btnSaveClick();
        else if (e.getSource() == btnCancel) this.dispose();
    }

    private void btnSaveClick() {
        String name = txtFullName.getText().trim();
        String cccd = txtIdCardNo.getText().trim();
        LocalDate dob = ValidationUtil.parseDate(txtDob.getText());
        String hometown = txtHometown.getText().trim();

        if (name.isEmpty() || cccd.isEmpty() || dob == null || hometown.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên, CCCD, Ngày sinh và Quê quán là bắt buộc!");
            return;
        }
        Resident r = new Resident();
        r.setFullName(name);
        r.setIdCardNo(cccd);
        r.setPhone(txtPhone.getText().trim());
        r.setDateOfBirth(dob);
        r.setHometown(hometown);
        r.setNote(txtNote.getText().trim());

        try (ResidentDAO rd = new ResidentDAO()) {
            if (rd.addResident(r)) {
                JOptionPane.showMessageDialog(this, "Thêm hồ sơ thành công!");
                parent.onResidentAdded(r);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Số CCCD đã tồn tại hoặc lỗi dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
