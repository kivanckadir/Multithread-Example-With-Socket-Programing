package clienttype2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import clienttype1.ClientType1;
import lists.Lists;
import net.miginfocom.swing.MigLayout;
import parsers.JobParser;

public class DetailFrame extends JDialog implements ActionListener{
	
	private String[] machineTableColumnNames = {"ID", "NAME", "TYPE", "SPEED", "TOTAL JOB COUNT", "TOTAL JOB TIME", "TOTAL WORK TIME"};
	private String jobsTableColumnNames[] = {"ID", "TYPE", "LENGTH"};
	
	//Modal
	private JFrame mParentFrame;
	private static String title = new String("Details");
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;
	
	//Components
	private JLabel machineJLabel;
	private JTable machineJTable;
	private JLabel jobsJLabel;
	private JTable jobsJTable;
	
	private ClientType1 clientType1;

	public DetailFrame(ClientType1 clientType1) {
		super();
		

		this.clientType1 = clientType1;
		layoutManager = new MigLayout("fill", "[grow]", "[][]20[][]");
		width = 500;
		height = 700;
		dimension = new Dimension(width, height);
		
		machineJLabel = new JLabel("Machine:");
		machineJTable = new JTable();
		jobsJLabel = new JLabel("Completed Jobs By Machine:");
		jobsJTable = new JTable();
		
		init();
	}
	
	
	public void init() {

		setSize(dimension);
		setTitle(title);
		setLayout(layoutManager);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		setLocationRelativeTo(mParentFrame);
		
		add(machineJLabel, "align left, grow, wrap");
		add(new JScrollPane(machineJTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "align center, growx, wrap");
		add(jobsJLabel, "align left, grow, wrap");
		add(new JScrollPane(jobsJTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "align center, growx, wrap");
		
		update();
	}
	
	
	public void update() {
		Object machineTableRow[][] = new Object[1][7];
		Object jobsTableRow[][] = new Object[clientType1.getJobs().size()][3];
		String speedStr = null;
		double speed = clientType1.getSpeed();
		
		machineTableRow[0][0] = clientType1.getId();
		machineTableRow[0][1] = clientType1.getName();
		machineTableRow[0][2] = clientType1.getType();
		
		if (speed == 0.5) {
			speedStr = "0.5x";
		} else if (speed == 1) {
			speedStr = "1x";
		} else if (speed == 1.5) {
			speedStr = "1.5x";
		} else if (speed == 2) {
			speedStr = "2x";
		}
		
		machineTableRow[0][3] = speedStr;
		machineTableRow[0][4] = String.valueOf(clientType1.getTotalJobCount());
		machineTableRow[0][5] = String.valueOf(clientType1.getTotalJobTime());
		machineTableRow[0][6] = String.valueOf(clientType1.getTotalWorkTime());
		
		
		for (int i = 0; i < clientType1.getJobs().size(); i++) {
			jobsTableRow[i][0] = clientType1.getJobs().get(i).getId();
			jobsTableRow[i][1] = clientType1.getJobs().get(i).getType();
			jobsTableRow[i][2] = clientType1.getJobs().get(i).getLength();
		}
		
		machineJTable.setModel(new DefaultTableModel(machineTableRow, machineTableColumnNames));
		jobsJTable.setModel(new DefaultTableModel(jobsTableRow, jobsTableColumnNames));
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
