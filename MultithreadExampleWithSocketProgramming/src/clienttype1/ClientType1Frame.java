package clienttype1;


import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import server.FrameBehavior;

public class ClientType1Frame extends JFrame implements FrameBehavior, ActionListener{
	
	private String types[] = {"CNC", "DÖKÜM", "KILIF", "KAPLAMA"};
	private String speeds[] = {"0.5x", "1x", "1.5x", "2x"};
	private String statusTypes[] = {"NOT CONNECTED", "EMPTY", "BUSY"};
	
	private static String title = new String("Ýþ Makinasý: Client Type 1");
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;
	
	private JLabel nameJLabel;
	private JTextField nameJTextField;
	private JLabel speedJLabel;
	private JComboBox<String> speedJCombobox;
	private JLabel typeJLabel;
	private JComboBox<String> typeJCombobox;
	private JLabel portJLabel;
	private JTextField portJTextField;
	private JButton connectJButton;
	private JScrollPane scrollPane;
	private JTextArea messageListJTextArea;
	private JLabel idJLabel;
	private int id;
	private JLabel totalJobCountJLabel;
	private int totalJobCount;
	private JLabel totalJobTimeJLabel;
	private int totalJobTime;
	private JLabel totalWorkTimeJLabel;
	private int totalWorkTime;
	private JLabel statusJLabel;
	private String status;
	
	private ClientType1 clientType1;
	
	private String consoleMessages;

	public ClientType1Frame() {
		super(title);
		
		layoutManager = new MigLayout("","[][grow]","[][][][][grow][]");
		width = 700;
		height = 800;
		dimension = new Dimension(width, height);
		
		nameJLabel = new JLabel("Name:");
		nameJTextField = new JTextField();
		speedJLabel = new JLabel("Speed:");
		speedJCombobox = new JComboBox<>(speeds);
		typeJLabel = new JLabel("Type: ");
		typeJCombobox = new JComboBox<>(types);
		portJLabel = new JLabel("Port:");
		portJTextField = new JTextField("5001");
		connectJButton = new JButton("Connect");
		messageListJTextArea = new JTextArea();
		scrollPane = new JScrollPane(messageListJTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		id = -1;
		idJLabel = new JLabel("ID: " + id);
		totalJobCount = 0;
		totalJobCountJLabel = new JLabel("Total Job Count: " + totalJobCount);
		totalJobTime = 0;
		totalJobTimeJLabel = new JLabel("Total Job Time: " + totalJobTime);
		totalWorkTime = 0;
		totalWorkTimeJLabel = new JLabel("Total Work Time: " + totalWorkTime);
		status = statusTypes[0];
		statusJLabel = new JLabel("Status: " + status);
		
		consoleMessages="";
		
		init();
	}
	
	public void init() {

		setSize(dimension);
		setLayout(layoutManager);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(nameJLabel, "align right");
		add(nameJTextField, "growx, wrap");
		add(typeJLabel, "align right");
		add(typeJCombobox, "growx, wrap");
		add(speedJLabel, "align right");
		add(speedJCombobox, "growx, wrap");
		add(portJLabel, "align right");
		add(portJTextField, "grow, split 2");
		add(connectJButton, "wrap, align right");
		add(scrollPane, "span 2, grow, wrap");
		add(idJLabel, "span 3, split 5, align left, growx");
		add(totalJobCountJLabel, "align center, growx");
		add(totalJobTimeJLabel, "align center, growx");
		add(totalWorkTimeJLabel, "align center, growx");
		add(statusJLabel, "align right");

		idJLabel.setVisible(false);
		speedJCombobox.setSelectedIndex(1);
		messageListJTextArea.setEditable(false);
		
		addListeners();
	}
	
	public void addListeners() {
		connectJButton.addActionListener(this);
	}
	
	@Override
    public void printResult(String message){
    	consoleMessages = consoleMessages + message;
    	messageListJTextArea.setText(consoleMessages);
    	repaint();
	}

	//listeners
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(connectJButton)) {
			clientType1 = new ClientType1(this);
			clientType1.connect();
			setPassive();
		}
	}
	
	public void setPassive() {
		nameJTextField.setEnabled(false);
		speedJCombobox.setEnabled(false);
		portJTextField.setEnabled(false);
		typeJCombobox.setEnabled(false);
		connectJButton.setEnabled(false);
	}
	
	public void setActive() {
		nameJTextField.setEnabled(true);
		speedJCombobox.setEnabled(true);
		portJTextField.setEnabled(true);
		typeJCombobox.setEnabled(true);
		connectJButton.setEnabled(true);
	}

	
	//Getters
	public int getId() {
		return id;
	}
	
	public JTextField getNameJTextField() {
		return nameJTextField;
	}

	public JComboBox<String> getTypeJCombobox() {
		return typeJCombobox;
	}

	public JComboBox<String> getSpeedJCombobox() {
		return speedJCombobox;
	}

	public JTextField getPortJTextField() {
		return portJTextField;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public void makeAction(String message) {
		if(message.startsWith("updateId")) {
			idJLabel.setText("ID: " + message.split(":")[1]);
			idJLabel.setVisible(true);
			status = statusTypes[1];
			statusJLabel.setText("Status: "+status);
			repaint();
			
		} else if(message.startsWith("updateCounters")) {
			totalJobCountJLabel.setText("Total Job Count: " + message.split(":")[1]);
			totalJobTimeJLabel.setText("Total Job Time: " + message.split(":")[2]);
			totalWorkTimeJLabel.setText("Total Work Time: " + message.split(":")[3]);
			repaint();
		}
		
		else if(message.startsWith("updateStatus")) {
			statusJLabel.setText("Status: " + message.split(":")[1]);
			repaint();
		}
	}
}