<<<<<<< HEAD
package boundary;
=======
package view.meterreading;
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.BuildingDAO;
import dao.MeterReadingDAO;
<<<<<<< HEAD
import entity.Building;
import entity.MeterReading;
import entity.User;

public class SearchMeterReadingFrm extends JFrame implements ActionListener{
    private ArrayList<Building> listBuilding;
    private ArrayList<MeterReading> listReading;
    private JTextField txtBuilding, txtMonth, txtStatus;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private SearchMeterReadingFrm mainFrm;

    public SearchMeterReadingFrm(User user){
        super("Search meter readings to verify");
        this.user = user;
        mainFrm = this;
        listReading = new ArrayList<MeterReading>();

        BuildingDAO bd = new BuildingDAO();
        listBuilding = bd.getActiveBuildings();

        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JLabel lblHome = new JLabel("Search meter readings to verify");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,20)));

        txtBuilding = new JTextField();
        txtMonth = new JTextField();
        txtStatus = new JTextField(MeterReading.STATUS_PENDING);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);

        JPanel pn1 = new JPanel();
        pn1.setLayout(new GridLayout(4,2));
        pn1.add(new JLabel("Building (code or name): "));		pn1.add(txtBuilding);
        pn1.add(new JLabel("Month (MM/yyyy): "));				pn1.add(txtMonth);
        pn1.add(new JLabel("Status (PENDING, APPROVED, REJECTED, VERIFYING): "));	pn1.add(txtStatus);
        pn1.add(new JLabel(""));								pn1.add(btnSearch);
        pnMain.add(pn1);
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new
            Dimension(scrollPane.getPreferredSize().width, 250));

        tblResult.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = tblResult.rowAtPoint(e.getPoint()); // get row
                    if (row >= 0 && row < listReading.size()) {
                        tblResultClick(row);
                    }
                }
        });

        pn2.add(scrollPane);
        pnMain.add(pn2);

        JPanel pnButton = new JPanel();
        pnButton.setLayout(new GridLayout(1, 2, 10, 0));
        btnBack = new JButton("Back");
        pnButton.add(new JLabel(""));
        pnButton.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(pnButton);

        btnBack.addActionListener(this);
        this.add(pnMain);
        this.setSize(750,500);
        this.setLocation(200,10);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked.equals(btnSearch)){
            btnSearchClick();
        } else if (btnClicked.equals(btnBack)) {
            new LandlordHomeFrm(user).setVisible(true);
            dispose();
        }
    }

    private void btnSearchClick() {
        //find the building
        String key = txtBuilding.getText().trim();
        Building building = null;
        for(Building b: listBuilding) {
            if(b.getCode().equalsIgnoreCase(key) || b.getName().equalsIgnoreCase(key)) {
                building = b;
                break;
            }
        }
        if(building == null) {
            JOptionPane.showMessageDialog(this, "The building does not exist!");
            return;
        }

        //parse the month MM/yyyy
        int month, year;
        try {
            String[] part = txtMonth.getText().trim().split("/");
            month = Integer.parseInt(part[0].trim());
            year = Integer.parseInt(part[1].trim());
            if(month < 1 || month > 12)
            throw new NumberFormatException();
        }catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "The month must be in the format MM/yyyy!");
            return;
        }

        String status = txtStatus.getText().trim().toUpperCase();
        if(!(status.equals(MeterReading.STATUS_PENDING) || status.equals(MeterReading.STATUS_APPROVED)
                || status.equals(MeterReading.STATUS_REJECTED) || status.equals(MeterReading.STATUS_VERIFYING))) {
            JOptionPane.showMessageDialog(this, "The status must be PENDING, APPROVED, REJECTED or VERIFYING!");
            return;
        }

        MeterReadingDAO mrd = new MeterReadingDAO();
        listReading = mrd.searchMeterReading(building.getId(), month, year, status);

        String[] columnNames = {"Id", "Room", "Type", "Month/Year", "Previous", "Current", "Status"};
        String[][] value = new String[listReading.size()][7];
        for(int i=0; i<listReading.size(); i++){
            MeterReading mr = listReading.get(i);
            value[i][0] = mr.getId() +"";
            value[i][1] = mr.getRoom().getCode();
            value[i][2] = mr.getType();
            value[i][3] = mr.getMonth() + "/" + mr.getYear();
            value[i][4] = mr.getPreviousValue() +"";
            value[i][5] = mr.getCurrentValue() +"";
            value[i][6] = mr.getStatus();
        }
        DefaultTableModel tableModel =
        new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //unable to edit cells
                return false;
            }
        };
        tblResult.setModel(tableModel);

        if(listReading.size() == 0)
        JOptionPane.showMessageDialog(this, "There is no meter reading to verify!");
    }

    private void tblResultClick(int r) {
        MeterReading mr = listReading.get(r);
        //pack the reviewer (the logged in user) into the reading to transfer to the next interfaces
        mr.setReviewedBy(user);
        (new ReadingDetailFrm(mr)).setVisible(true);
        mainFrm.dispose();
    }
=======
import model.Building;
import model.MeterReading;
import model.User;

public class SearchMeterReadingFrm extends JFrame implements ActionListener{
	private ArrayList<Building> listBuilding;
	private ArrayList<MeterReading> listReading;
	private JTextField txtBuilding, txtMonth, txtStatus;
	private JButton btnSearch;
	private JTable tblResult;
	private User user;
	private SearchMeterReadingFrm mainFrm;
	
	public SearchMeterReadingFrm(User user){
		super("Search meter readings to verify");
		this.user = user;
		mainFrm = this;
		listReading = new ArrayList<MeterReading>();
		
		//load the buildings: used to convert the typed building into its id
		BuildingDAO bd = new BuildingDAO();
		listBuilding = bd.getBuilding();
		
		JPanel pnMain = new JPanel();
		pnMain.setSize(this.getSize().width-5, this.getSize().height-20);		
		pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
		pnMain.add(Box.createRigidArea(new Dimension(0,10)));
		
		JLabel lblHome = new JLabel("Search meter readings to verify");
		lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);	
		lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
		pnMain.add(lblHome);
		pnMain.add(Box.createRigidArea(new Dimension(0,20)));
		
		txtBuilding = new JTextField();
		txtMonth = new JTextField();
		txtStatus = new JTextField(MeterReading.STATUS_PENDING);
		btnSearch = new JButton("Search");
		btnSearch.addActionListener(this);
		
		JPanel pn1 = new JPanel();
		pn1.setLayout(new GridLayout(4,2));
		pn1.add(new JLabel("Building (code or name): "));		pn1.add(txtBuilding);
		pn1.add(new JLabel("Month (MM/yyyy): "));				pn1.add(txtMonth);
		pn1.add(new JLabel("Status (PENDING, APPROVED, REJECTED, VERIFYING): "));	pn1.add(txtStatus);
		pn1.add(new JLabel(""));								pn1.add(btnSearch);
		pnMain.add(pn1);
		pnMain.add(Box.createRigidArea(new Dimension(0,10)));

		JPanel pn2 = new JPanel();
		pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));		
		tblResult = new JTable();
		JScrollPane scrollPane= new  JScrollPane(tblResult);
		tblResult.setFillsViewportHeight(false); 
		scrollPane.setPreferredSize(new
               Dimension(scrollPane.getPreferredSize().width, 250));
		
		tblResult.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = tblResult.rowAtPoint(e.getPoint()); // get row 
				if (row >= 0 && row < listReading.size()) {
					tblResultClick(row);
				}
			}
		});

		pn2.add(scrollPane);
		pnMain.add(pn2);	
		this.add(pnMain);
		this.setSize(750,500);				
		this.setLocation(200,10);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btnClicked = (JButton)e.getSource();
		if(btnClicked.equals(btnSearch)){
			btnSearchClick();
		}
	}
	
	private void btnSearchClick() {
		//find the building
		String key = txtBuilding.getText().trim();
		Building building = null;
		for(Building b: listBuilding) {
			if(b.getCode().equalsIgnoreCase(key) || b.getName().equalsIgnoreCase(key)) {
				building = b;
				break;
			}
		}
		if(building == null) {
			JOptionPane.showMessageDialog(this, "The building does not exist!");
			return;
		}
		
		//parse the month MM/yyyy
		int month, year;
		try {
			String[] part = txtMonth.getText().trim().split("/");
			month = Integer.parseInt(part[0].trim());
			year = Integer.parseInt(part[1].trim());
			if(month < 1 || month > 12)
				throw new NumberFormatException();
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, "The month must be in the format MM/yyyy!");
			return;
		}
		
		String status = txtStatus.getText().trim().toUpperCase();
		if(!(status.equals(MeterReading.STATUS_PENDING) || status.equals(MeterReading.STATUS_APPROVED)
				|| status.equals(MeterReading.STATUS_REJECTED) || status.equals(MeterReading.STATUS_VERIFYING))) {
			JOptionPane.showMessageDialog(this, "The status must be PENDING, APPROVED, REJECTED or VERIFYING!");
			return;
		}
		
		MeterReadingDAO mrd = new MeterReadingDAO();
		listReading = mrd.searchMeterReading(building.getId(), month, year, status);
		
		String[] columnNames = {"Id", "Room", "Type", "Month/Year", "Previous", "Current", "Status"};
		String[][] value = new String[listReading.size()][7];
		for(int i=0; i<listReading.size(); i++){
			MeterReading mr = listReading.get(i);
			value[i][0] = mr.getId() +"";
			value[i][1] = mr.getRoom().getCode();
			value[i][2] = mr.getType();
			value[i][3] = mr.getMonth() + "/" + mr.getYear();
			value[i][4] = mr.getPreviousValue() +"";
			value[i][5] = mr.getCurrentValue() +"";
			value[i][6] = mr.getStatus();
		}
		DefaultTableModel tableModel = 
               new DefaultTableModel(value, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //unable to edit cells
		       return false;
		    }
		};
		tblResult.setModel(tableModel);
		
		if(listReading.size() == 0)
			JOptionPane.showMessageDialog(this, "There is no meter reading to verify!");
	}
	
	private void tblResultClick(int r) {
		MeterReading mr = listReading.get(r);
		//pack the reviewer (the logged in user) into the reading to transfer to the next interfaces
		mr.setReviewedBy(user);
		(new ReadingDetailFrm(mr)).setVisible(true);
		mainFrm.dispose();
	}
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
}
