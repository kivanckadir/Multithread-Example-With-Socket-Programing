package clienttype2;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import clienttype1.ClientType1;
import job.Job;
import net.miginfocom.swing.MigLayout;
import parsers.ClientType1Parser;
import parsers.JobParser;
import parsers.XmlParser;
import server.FrameBehavior;

public class ClientType2Frame extends JFrame implements FrameBehavior, ActionListener {
	
	private static String title = new String("Planning Application: Client Type 2");
	
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;

	private JLabel machinesJLabel;
	private JTable machinesJTable;
	private JLabel jobsJLabel;
	private JTable jobsJTable;
	private JButton newJobJButton;
	private JPanel emptyMachineListJPanel;
	private JLabel statusJLabel;
	private String status;
	private JLabel idJLabel;
	private int id;

	private ClientType2 clientType2;

	public ClientType2Frame(ClientType2 clientType2) {
		super(title);
		
		layoutManager = new MigLayout("fill", "[grow]", "[][]20[][]20[]20[][]");
		width = 900;
		height = 600;
		dimension = new Dimension(width, height);
		machinesJLabel = new JLabel("Active Machines:");
		machinesJTable = new JTable();
		machinesJTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jobsJLabel = new JLabel("Pending Jobs:");
		jobsJTable = new JTable();
		newJobJButton = new JButton("New Job");
		id = -1;
		idJLabel = new JLabel("ID: " + id);
		status = "NOT CONNECTED";
		statusJLabel = new JLabel("Status: " + status);

		
		this.clientType2 = clientType2;

		init();
	}

	public void init() {

	
		setSize(dimension);
		setLayout(layoutManager);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(machinesJLabel, "align left, grow, wrap");
		add(new JScrollPane(machinesJTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "align center, growx, wrap");
		add(jobsJLabel, "align left, grow, wrap");
		add(new JScrollPane(jobsJTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "align center, growx, wrap");
		add(newJobJButton, "align center, grow, wrap");
		add(idJLabel, "align left, span2, split 2");
		add(statusJLabel, "align right");

		idJLabel.setVisible(false);

		addListeners();

	}

	public void addListeners() {
		newJobJButton.addActionListener(this);
	}

	@Override
	public void printResult(String message) {

	}

	// listeners
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newJobJButton)) {
			
			new NewJobFrame(this, clientType2).setVisible(true);
		}
	}

	@Override
	public void makeAction(String message) {
		if (message.startsWith("updateId")) {
			idJLabel.setText("ID: " + message.split(":")[1]);
			idJLabel.setVisible(true);
			repaint();

		} else if (message.startsWith("updateStatus")) {
			status = message.split(":")[1];
			statusJLabel.setText(status);
			repaint();
			
		} else if(message.startsWith("updateMachineTable")) {
			String source = message.split(":")[1];
			
			String itemsStr = XmlParser.get(source, "items");
			String itemsArray[] = itemsStr.split("ClientType1");
			ArrayList<ClientType1> clietType1List = new ArrayList<ClientType1>();
			
			
			for (int i = 1; i < itemsArray.length; i++) {
				ClientType1 clientType1 = ClientType1Parser.getAsObject(itemsArray[i]);
				clietType1List.add(clientType1);
			}
			
			String[] machineTableColumnNames = {"ID", "NAME", "TYPE", "SPEED", "STATUS", "TOTAL JOB COUNT", "TOTAL JOB TIME", "TOTAL WORK TIME", "DETAIL"};
			String[][] machineTableRows = new String[clietType1List.size()][10];
			
			for (int i = 0; i < clietType1List.size(); i++) {
				
				String speedStr = null;
				double speed = clietType1List.get(i).getSpeed();
				
				machineTableRows[i][0] = String.valueOf(clietType1List.get(i).getId());
				machineTableRows[i][1] = clietType1List.get(i).getName();
				machineTableRows[i][2] = clietType1List.get(i).getType();
				
				if (speed == 0.5) {
					speedStr = "0.5x";
				} else if (speed == 1) {
					speedStr = "1x";
				} else if (speed == 1.5) {
					speedStr = "1.5x";
				} else if (speed == 2) {
					speedStr = "2x";
				}
				
				machineTableRows[i][3] = speedStr;
				machineTableRows[i][4] = clietType1List.get(i).getStatus();
				machineTableRows[i][5] = String.valueOf(clietType1List.get(i).getTotalJobCount());
				machineTableRows[i][6] = String.valueOf(clietType1List.get(i).getTotalJobTime());
				machineTableRows[i][7] = String.valueOf(clietType1List.get(i).getTotalWorkTime());
				machineTableRows[i][8] = "See";
				
			}
			
			DefaultTableModel defaultTableModel = new DefaultTableModel(machineTableRows, machineTableColumnNames);
			defaultTableModel.fireTableDataChanged();
			machinesJTable.setModel(defaultTableModel);
			
			Action detail = new AbstractAction()
			{
			    public void actionPerformed(ActionEvent e)
			    {
			        JTable table = (JTable) e.getSource();
			        int modelRow = Integer.valueOf( e.getActionCommand());
			        
			        int selectedClientId = Integer.parseInt((String) defaultTableModel.getValueAt(modelRow, 0));
			        for (int i = 0; i < clietType1List.size(); i++) {
						if(clietType1List.get(i).getId()==selectedClientId) {
							ClientType1 selectedClient = clietType1List.get(i);
							
							System.out.println(selectedClient.getJobs().toString());
							DetailFrame detailFrame = new DetailFrame(selectedClient);
							detailFrame.setVisible(true);
						}
					}
			    }
			};
			 
			ButtonColumn buttonColumn = new ButtonColumn(machinesJTable, detail, 8);
			buttonColumn.setMnemonic(KeyEvent.VK_D);
			repaint();
		}
		
		else if(message.startsWith("updateJobTable")) {
			String source = message.split(":")[1];

			String itemsStr = XmlParser.get(source, "items");
			String itemsArray[] = itemsStr.split("Job");
			ArrayList<Job> jobList = new ArrayList<Job>();
			
			for (int i = 1; i < itemsArray.length; i++) {
				Job job = JobParser.getAsObject(itemsArray[i]);
				jobList.add(job);
			}
			
			System.out.println(jobList.toString());
			String[] jobTableColumnNames = {"ID", "TYPE", "LENGTH"};
			String[][] jobTableRows = new String[jobList.size()][3];
			
			for (int i = 0; i < jobList.size(); i++) {
				
				jobTableRows[i][0] = String.valueOf(jobList.get(i).getId());
				jobTableRows[i][1] = jobList.get(i).getType();
				jobTableRows[i][2] = String.valueOf(jobList.get(i).getLength());

			}
		
			DefaultTableModel defaultTableModel = new DefaultTableModel(jobTableRows, jobTableColumnNames);
			jobsJTable.setModel(defaultTableModel);

			repaint();
		}
	}
}