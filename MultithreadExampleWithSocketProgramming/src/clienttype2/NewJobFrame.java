package clienttype2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import lists.Lists;
import net.miginfocom.swing.MigLayout;

public class NewJobFrame extends JDialog implements ActionListener{
	
	private String types[] = {"CNC", "DÖKÜM", "KILIF", "KAPLAMA"};
	
	//Modal
	private JFrame mParentFrame;
	private static String title = new String("New Job");
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;
	
	//Components
	private JLabel newJobJLabel;
	private JLabel jobLengthJLabel;
	private JTextField jobLengthJTextField;
	private JLabel jobTypeJLabel;
	private JComboBox<String> jobTypeJComboBox;
	private JButton saveJButton;
	private JButton cancelJButton;
	
	private ClientType2 clientType2;

	public NewJobFrame(ClientType2Frame parentFrame, ClientType2 clientType2) {
		super();
		
		mParentFrame = parentFrame;
		this.clientType2 = clientType2;
		layoutManager = new MigLayout("", "[]5[grow]", "[]20[][][grow]");
		width = 400;
		height = 180;
		dimension = new Dimension(width, height);
		
		newJobJLabel = new JLabel("Please enter length & type.");
		jobLengthJLabel = new JLabel("Length (millis):");
		jobLengthJTextField = new JTextField();
		jobTypeJLabel = new JLabel("Type:");
		jobTypeJComboBox = new JComboBox(types);
		saveJButton = new JButton("SAVE");
		cancelJButton = new JButton("CANCEL");
		
		init();
	}
	
	public void init() {

		setSize(dimension);
		setTitle(title);
		setLayout(layoutManager);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setModal(true);
		setResizable(false);
		
		setLocationRelativeTo(mParentFrame);
		
		add(newJobJLabel, "span 2, align center, wrap");
		add(jobLengthJLabel, "align right");
		add(jobLengthJTextField, "growx, wrap");
		add(jobTypeJLabel, "align right");
		add(jobTypeJComboBox, "growx, wrap");
		add(saveJButton, "align right, bottom, span 2, split 2");
		add(cancelJButton, "align right, bottom");
		
		addListeners();
	}
	
	public void addListeners() {
		saveJButton.addActionListener(this);
		cancelJButton.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(saveJButton)) {
			new JobSenderThread(clientType2, jobLengthJTextField.getText(), jobTypeJComboBox.getSelectedItem().toString()).start();
			
		} else if(e.getSource().equals(cancelJButton)) {
			dispose();
		}
	}
}
