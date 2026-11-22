package boundary;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import dao.MeterReadingDAO;
import entity.MeterReading;
import entity.User;

public class ReadingDetailFrm extends JFrame implements ActionListener{
    private JLabel lblRoomInfo, lblReadingInfo, lblEInvoiceValue, lblDeviation, lblEvidenceImage;
    private JTextArea txtRejectReason;
    private JButton btnApprove, btnReject, btnRequestVerification, btnBack;
    private MeterReading mr;

    public ReadingDetailFrm(MeterReading mr){
        super("Meter reading detail");

        //get the full information of the reading, keep the reviewer transferred from the previous interface
        User reviewer = mr.getReviewedBy();
        MeterReadingDAO mrd = new MeterReadingDAO();
        MeterReading detail = mrd.getReadingDetail(mr.getId());
        if(detail != null) {
            detail.setReviewedBy(reviewer);
            this.mr = detail;
        }else
        this.mr = mr;

        lblRoomInfo = new JLabel();
        lblReadingInfo = new JLabel();
        lblEInvoiceValue = new JLabel();
        lblDeviation = new JLabel();
        lblEvidenceImage = new JLabel("", JLabel.CENTER);
        lblEvidenceImage.setPreferredSize(new Dimension(300, 200));
        txtRejectReason = new JTextArea(3, 30);
        btnApprove = new JButton("Approve");
        btnReject = new JButton("Reject");
        btnRequestVerification = new JButton("Request verification");
        btnBack = new JButton("Back");

        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JLabel lblHome = new JLabel("Meter reading detail");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,15)));

        pnMain.add(wrap(lblRoomInfo));
        pnMain.add(wrap(lblReadingInfo));
        pnMain.add(wrap(lblEInvoiceValue));
        pnMain.add(wrap(lblDeviation));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
        pnMain.add(wrap(lblEvidenceImage));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel pnReason = new JPanel();
        pnReason.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnReason.add(new JLabel("Reject reason:"));
        pnReason.add(new JScrollPane(txtRejectReason));
        pnMain.add(pnReason);

        JPanel pnButton = new JPanel();
        pnButton.setLayout(new FlowLayout());
        pnButton.add(btnApprove);
        pnButton.add(btnReject);
        pnButton.add(btnRequestVerification);
        pnButton.add(btnBack);
        pnMain.add(pnButton);

        btnApprove.addActionListener(this);
        btnReject.addActionListener(this);
        btnRequestVerification.addActionListener(this);
        btnBack.addActionListener(this);

        initForm();
        this.setContentPane(pnMain);
        this.setSize(650,650);
        this.setLocation(200,10);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel wrap(JLabel lbl) {
        JPanel pn = new JPanel();
        pn.setLayout(new FlowLayout(FlowLayout.LEFT));
        pn.add(lbl);
        return pn;
    }

    /**
    * display the information of the reading, including its deviation
    */
    private void initForm() {
        lblRoomInfo.setText("Room: " + mr.getRoom().getCode() + " (area " + mr.getRoom().getArea() + " m2)");
        lblReadingInfo.setText(mr.getType() + " " + mr.getMonth() + "/" + mr.getYear()
            + ": previous " + mr.getPreviousValue() + ", current " + mr.getCurrentValue()
            + ", consumption " + mr.getConsumption() + ", status " + mr.getStatus());
        if(mr.getEInvoiceValue() == null) {
            lblEInvoiceValue.setText("E-invoice value: not available");
            lblDeviation.setText("Deviation: not available");
        }else {
            lblEInvoiceValue.setText("E-invoice value: " + mr.getEInvoiceValue());
            lblDeviation.setText("Deviation (current - e-invoice): " + String.format("%.2f", mr.calcDeviation()));
        }
        showEvidenceImage();
    }

    private void showEvidenceImage() {
        String url = mr.getEvidenceImageUrl();
        if(url == null || url.trim().length() == 0) {
            lblEvidenceImage.setText("No evidence image");
            return;
        }
        try {
            ImageIcon icon = url.toLowerCase().startsWith("http") ? new ImageIcon(URI.create(url).toURL()) : new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            lblEvidenceImage.setIcon(new ImageIcon(img));
        }catch(Exception ex) {
            lblEvidenceImage.setText("Cannot load the evidence image: " + url);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked.equals(btnApprove)) {
            btnApproveClick();
        }else if(btnClicked.equals(btnReject)) {
            btnRejectClick();
        }else if(btnClicked.equals(btnRequestVerification)) {
            btnRequestVerificationClick();
        }else if(btnClicked.equals(btnBack)) {
            btnBackClick();
        }
    }

    private boolean checkPending() {
        if(!MeterReading.STATUS_PENDING.equals(mr.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only a PENDING reading can be verified!");
            return false;
        }
        return true;
    }

    private void btnApproveClick() {
        if(!checkPending())
        return;
        MeterReadingDAO mrd = new MeterReadingDAO();
        if(mrd.approve(mr.getId(), mr.getReviewedBy().getId())) {
            JOptionPane.showMessageDialog(this, "The meter reading is successfully approved!");
            btnBackClick();
        }else
        JOptionPane.showMessageDialog(this, "Cannot approve the meter reading!");
    }

    private void btnRejectClick() {
        if(!checkPending())
        return;
        String reason = txtRejectReason.getText().trim();
        if(reason.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please enter the reject reason!");
            return;
        }
        MeterReadingDAO mrd = new MeterReadingDAO();
        if(mrd.reject(mr.getId(), mr.getReviewedBy().getId(), reason)) {
            JOptionPane.showMessageDialog(this, "The meter reading is successfully rejected!");
            btnBackClick();
        }else
        JOptionPane.showMessageDialog(this, "Cannot reject the meter reading!");
    }

    private void btnRequestVerificationClick() {
        if(!checkPending())
        return;
        (new RequestVerificationFrm(mr)).setVisible(true);
        this.dispose();
    }

    private void btnBackClick() {
        (new SearchMeterReadingFrm(mr.getReviewedBy())).setVisible(true);
        this.dispose();
    }
}
