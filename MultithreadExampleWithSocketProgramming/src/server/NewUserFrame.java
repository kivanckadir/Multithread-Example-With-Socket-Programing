package server;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import lists.Lists;
import net.miginfocom.swing.MigLayout;

public class NewUserFrame extends JDialog implements ActionListener{
	
	//Modal
	private JFrame mParentFrame;
	private static String title = new String("New User");
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;
	
	//Components
	private JLabel newUserJLabel;
	private JLabel userNameJLabel;
	private JTextField userNameJTextField;
	private JLabel passwordJLabel;
	private JTextField passwordJTextField;
	private JButton saveJButton;
	private JButton cancelJButton;

	public NewUserFrame(ServerFrame parentFrame) {
		super();
		
		mParentFrame = parentFrame;
		layoutManager = new MigLayout("", "[]5[grow]", "[]20[][][grow]");
		width = 400;
		height = 160;
		dimension = new Dimension(width, height);
		
		newUserJLabel = new JLabel("Please enter username & password of new user.");
		userNameJLabel = new JLabel("User Name:");
		userNameJTextField = new JTextField();
		passwordJLabel = new JLabel("Password:");
		passwordJTextField = new JPasswordField();
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
		
		add(newUserJLabel, "span 2, align center, wrap");
		add(userNameJLabel, "align right");
		add(userNameJTextField, "growx, wrap");
		add(passwordJLabel, "align right");
		add(passwordJTextField, "growx, wrap");
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
			Lists.usernamePassword.put(userNameJTextField.getText(), passwordJTextField.getText());
			JDialog jDialog = new JDialog(mParentFrame, "Saved", true);
			jDialog.setResizable(false);
			jDialog.setLocationRelativeTo(this);
			dispose();
			
			jDialog.setVisible(true);
			
		} else if(e.getSource().equals(cancelJButton)) {
			dispose();
		}
	}
}
