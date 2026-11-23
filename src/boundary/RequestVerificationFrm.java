package boundary;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import dao.MeterReadingDAO;
import dao.ReadingVerificationDAO;
import entity.MeterReading;
import entity.ReadingVerification;

public class RequestVerificationFrm extends JFrame implements ActionListener{
    private JTextArea txtReason, txtRequest;
    private JButton btnSubmit, btnBack;
    private MeterReading mr;

    public RequestVerificationFrm(MeterReading mr){
        super("Gửi yêu cầu xác minh");
        this.mr = mr;

        txtReason = new JTextArea(3, 30);
        txtRequest = new JTextArea(3, 30);
        btnSubmit = new JButton("Gửi yêu cầu");

        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JLabel lblHome = new JLabel("Gửi yêu cầu xác minh");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,15)));

        JLabel lblInfo = new JLabel("Room " + mr.getRoom().getCode() + ", " + mr.getType() + " "
            + mr.getMonth() + "/" + mr.getYear() + ", chỉ số hiện tại " + mr.getCurrentValue());
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblInfo);
        pnMain.add(Box.createRigidArea(new Dimension(0,15)));

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(2,2));
        content.add(new JLabel("Lý do:"));				content.add(new JScrollPane(txtReason));
        content.add(new JLabel("Nội dung yêu cầu:"));	content.add(new JScrollPane(txtRequest));
        pnMain.add(content);

        JPanel pnButton = new JPanel();
        pnButton.setLayout(new FlowLayout());
        btnBack = new JButton("Quay lại");
        pnButton.add(btnSubmit);
        pnButton.add(btnBack);
        pnMain.add(pnButton);
        btnSubmit.addActionListener(this);
        btnBack.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(600,350);
        this.setLocation(200,10);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked.equals(btnSubmit)) {
            btnSubmitClick();
        } else if (btnClicked.equals(btnBack)) {
            new ReadingDetailFrm(mr).setVisible(true);
            dispose();
        }
    }

    private void btnSubmitClick() {
        String reason = txtReason.getText().trim();
        String request = txtRequest.getText().trim();
        if(reason.length() == 0 || request.length() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do và nội dung yêu cầu.");
            return;
        }

        //pack the verification
        ReadingVerification v = new ReadingVerification(reason, request);

        //save the verification and set the status of the reading in a single transaction
        ReadingVerificationDAO rvd = new ReadingVerificationDAO();
        MeterReadingDAO mrd = new MeterReadingDAO();
        rvd.beginTransaction();
        if(rvd.addVerification(mr.getId(), v)
            && mrd.updateStatus(mr.getId(), MeterReading.STATUS_VERIFYING)) {
            rvd.commit();
            mr.setStatus(MeterReading.STATUS_VERIFYING);
            (new ResolveVerificationFrm(mr)).setVisible(true);
            this.dispose();
        }else {
            rvd.rollback();
            JOptionPane.showMessageDialog(this, "Không thể lưu yêu cầu xác minh.");
        }
    }
}
