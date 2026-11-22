package view.user;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.User;
import view.meterreading.SearchMeterReadingFrm;

public class LandlordHomeFrm extends JFrame implements ActionListener{
	private JButton btnVerifyMeterReading;
	private User user;

	public LandlordHomeFrm(User user) {
		super("Landlord home");	
		this.user = user;

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        
		JPanel lblPane = new JPanel();
		lblPane.setLayout(new BoxLayout(lblPane, BoxLayout.LINE_AXIS));
		lblPane.add(Box.createRigidArea(new Dimension(450, 0)));
		JLabel lblUsername = new JLabel("Logged in as: " + user.getFullName());
		lblUsername.setAlignmentX(Component.RIGHT_ALIGNMENT);	
		lblPane.add(lblUsername);
		listPane.add(lblPane);
		listPane.add(Box.createRigidArea(new Dimension(0,20)));
		
		JLabel lblHome = new JLabel("Landlord's home");
		lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);	
		lblHome.setFont (lblHome.getFont ().deriveFont (28.0f));
		listPane.add(lblHome);
		listPane.add(Box.createRigidArea(new Dimension(0,20)));
		
		btnVerifyMeterReading = new JButton("Verify meter readings");
		btnVerifyMeterReading.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnVerifyMeterReading.addActionListener(this);
		listPane.add(btnVerifyMeterReading);
		
		this.setSize(600,300);				
		this.setLocation(200,10);
		this.add(listPane, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if((e.getSource() instanceof JButton)&&
                 (((JButton)e.getSource()).equals(btnVerifyMeterReading))) {
			btnVerifyMeterReadingClick();
		}
	}
	
	private void btnVerifyMeterReadingClick() {
		(new SearchMeterReadingFrm(user)).setVisible(true);
		this.dispose();
	}
}
