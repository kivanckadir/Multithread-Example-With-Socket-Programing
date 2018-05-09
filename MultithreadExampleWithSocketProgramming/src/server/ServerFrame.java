package server;




import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class ServerFrame extends JFrame implements FrameBehavior, ActionListener{
	
	private static String title = new String("Server");
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;
	
	private JButton newUserJButton;
	private JLabel portJLabel;
	private JTextField portJTextField;
	private JButton startJButton;
	private JScrollPane scrollPane;
	private JTextArea messageListJTextArea;
	
	private String consoleMessages;
	
	public ServerFrame() {
		super(title);
		
		layoutManager = new MigLayout("fill","[center, grow]","[]10[]10[grow]");
		width = 700;
		height = 800;
		dimension = new Dimension(width, height);
		newUserJButton = new JButton("New User");
		portJLabel = new JLabel("Port:");
		portJTextField = new JTextField("5001");
		startJButton = new JButton("Start Server");
		messageListJTextArea = new JTextArea();
		scrollPane = new JScrollPane(messageListJTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		consoleMessages="";
		
		init();
	}
	
	public void init() {

		setSize(dimension);
		setLayout(layoutManager);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);
		
		add(newUserJButton, "growx, wrap");
		add(portJLabel, "align left, gap right 10, split 3");
		add(portJTextField, "grow");
		add(startJButton, "align right, wrap");
		add(scrollPane, "grow");
		
		messageListJTextArea.setEditable(false);
		
		addListeners();
	}
	
	public void addListeners() {
		newUserJButton.addActionListener(this);
		startJButton.addActionListener(this);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(newUserJButton)) {
			new NewUserFrame(this).setVisible(true);
			
		} else if(e.getSource().equals(startJButton)) {
			try {
				int port = Integer.parseInt(portJTextField.getText());
				new Server(this).startServer();
				startJButton.setEnabled(false);
				portJTextField.setEnabled(false);
				printResult("Server Baþladý.\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public JTextField getPortJTextField() {
		return portJTextField;
	}
	
	@Override
    public void printResult(String message){
    	consoleMessages = consoleMessages + message;
    	messageListJTextArea.setText(consoleMessages);
    	repaint();
	}

	@Override
	public void makeAction(String message) {

	}
}