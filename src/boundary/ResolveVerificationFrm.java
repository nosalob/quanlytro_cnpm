package boundary;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import dao.MeterReadingDAO;
import dao.ReadingVerificationDAO;
import entity.MeterReading;
import entity.ReadingVerification;

public class ResolveVerificationFrm extends JFrame implements ActionListener{
    private JLabel lblRequestInfo, lblVerificationInfo;
    private JTextField txtActualValue;
    private JTextArea txtConclusion;
    private JButton btnResolve, btnBack;
    private MeterReading mr;
    private ReadingVerification verification;

    public ResolveVerificationFrm(MeterReading mr){
        super("Xử lý yêu cầu xác minh");
        this.mr = mr;

        //get the open verification of the reading
        ReadingVerificationDAO rvd = new ReadingVerificationDAO();
        verification = rvd.getVerificationByReading(mr.getId());

        lblRequestInfo = new JLabel();
        lblVerificationInfo = new JLabel();
        txtActualValue = new JTextField(15);
        txtConclusion = new JTextArea(3, 30);
        btnResolve = new JButton("Xử lý");
        btnBack = new JButton("Quay lại");

        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JLabel lblHome = new JLabel("Xử lý yêu cầu xác minh");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,15)));

        pnMain.add(wrap(lblRequestInfo));
        pnMain.add(wrap(lblVerificationInfo));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(2,2));
        content.add(new JLabel("Giá trị thực tế:"));		content.add(txtActualValue);
        content.add(new JLabel("Kết luận:"));			content.add(new JScrollPane(txtConclusion));
        pnMain.add(content);

        JPanel pnButton = new JPanel();
        pnButton.setLayout(new FlowLayout());
        pnButton.add(btnBack);
        pnButton.add(btnResolve);
        pnMain.add(pnButton);
        btnBack.addActionListener(this);
        btnResolve.addActionListener(this);

        initForm();
        this.setContentPane(pnMain);
        this.setSize(650,400);
        this.setLocation(200,10);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel wrap(JLabel lbl) {
        JPanel pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        pn.add(lbl);
        return pn;
    }

    private void initForm() {
        lblRequestInfo.setText("Reading: room " + mr.getRoom().getCode() + ", " + mr.getType() + " "
            + mr.getMonth() + "/" + mr.getYear() + ", chỉ số hiện tại " + mr.getCurrentValue());
        if(verification == null) {
            lblVerificationInfo.setText("Chỉ số này không có yêu cầu xác minh đang chờ xử lý.");
            btnResolve.setEnabled(false);
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date = verification.getRequestedAt() == null ? "" : sdf.format(verification.getRequestedAt());
            lblVerificationInfo.setText("Verification: " + verification.getReason()
                + " | request: " + verification.getMethod() + " | requested at " + date);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked.equals(btnResolve)) {
            btnResolveClick();
        } else if (btnClicked.equals(btnBack)) {
            new SearchMeterReadingFrm(mr.getReviewedBy()).setVisible(true);
            dispose();
        }
    }

    private void btnResolveClick() {
        double actualValue;
        try {
            actualValue = Double.parseDouble(txtActualValue.getText().trim());
        }catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Giá trị thực tế phải là một số.");
            return;
        }
        String conclusion = txtConclusion.getText().trim();
        if(conclusion.length() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập kết luận.");
            return;
        }
        verification.setResult(conclusion);

        //close the verification, update the value and put the reading back to PENDING in a single transaction
        ReadingVerificationDAO rvd = new ReadingVerificationDAO();
        MeterReadingDAO mrd = new MeterReadingDAO();
        rvd.beginTransaction();
        if(rvd.resolveVerification(verification)
            && mrd.updateValue(mr.getId(), actualValue)
            && mrd.updateStatus(mr.getId(), MeterReading.STATUS_PENDING)) {
            rvd.commit();
            JOptionPane.showMessageDialog(this, "Đã xử lý yêu cầu xác minh.");
            (new SearchMeterReadingFrm(mr.getReviewedBy())).setVisible(true);
            this.dispose();
        }else {
            rvd.rollback();
            JOptionPane.showMessageDialog(this, "Không thể lưu kết quả xác minh.");
        }
    }
}
