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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import server.FrameBehavior;

public class LoginFrame extends JDialog implements FrameBehavior, ActionListener{
	

	private static String title = new String("Planning Application: Login");
	private LayoutManager layoutManager;
	private Dimension dimension;
	private int width, height;
	
	//Components
	private JLabel loginJLabel;
	private JLabel userNameJLabel;
	private JTextField userNameJTextField;
	private JLabel passwordJLabel;
	private JPasswordField passwordJTextField;
	private JLabel portJLabel;
	private JTextField portJTextField;
	private JButton loginJButton;
	private JButton cancelJButton;
	
	private ClientType2 clientType2;

	public LoginFrame() {
		super();
		
		layoutManager = new MigLayout("", "[]5[grow]", "[]20[][][][grow]");
		width = 400;
		height = 180;
		dimension = new Dimension(width, height);
		
		loginJLabel = new JLabel("Please enter username & password.");
		userNameJLabel = new JLabel("User Name:");
		userNameJTextField = new JTextField();
		passwordJLabel = new JLabel("Password:");
		passwordJTextField = new JPasswordField();
		portJLabel = new JLabel("Port:");
		portJTextField = new JTextField("5001");
		loginJButton = new JButton("LOGIN");
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
		
		
		add(loginJLabel, "span 2, align center, wrap");
		add(userNameJLabel, "align right");
		add(userNameJTextField, "growx, wrap");
		add(passwordJLabel, "align right");
		add(passwordJTextField, "growx, wrap");
		add(portJLabel, "align right");
		add(portJTextField, "growx, wrap");
		add(loginJButton, "align right, bottom, span 2, split 2");
		add(cancelJButton, "align right, bottom");
		
		addListeners();
	}
	
	public void addListeners() {
		loginJButton.addActionListener(this);
		cancelJButton.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(loginJButton)) {
			clientType2 = new ClientType2(this);
			clientType2.connect();
			
		} else if(e.getSource().equals(cancelJButton)) {
			dispose();
		}
	}
	
	public JTextField getUserNameJTextField() {
		return userNameJTextField;
	}

	public JPasswordField getPasswordJTextField() {
		return passwordJTextField;
	}

	public JTextField getPortJTextField() {
		return portJTextField;
	}

	public JButton getLoginJButton() {
		return loginJButton;
	}

	public void setUserNameJTextField(JTextField userNameJTextField) {
		this.userNameJTextField = userNameJTextField;
	}

	public void setPasswordJTextField(JPasswordField passwordJTextField) {
		this.passwordJTextField = passwordJTextField;
	}

	public void setPortJTextField(JTextField portJTextField) {
		this.portJTextField = portJTextField;
	}

	public void setLoginJButton(JButton loginJButton) {
		this.loginJButton = loginJButton;
	}

	@Override
	public void printResult(String consoleMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeAction(String message) {
		if(message.equals("loginError")) {
			JDialog jDialog = new JDialog(this, "Username or password is wrong", true);
			jDialog.setSize(340, 0);
			jDialog.setLocationRelativeTo(this);
			jDialog.setVisible(true);
			repaint();
			
		} else if(message.equals("inUse")) {
			JDialog jDialog = new JDialog(this, "Already connected", true);
			jDialog.setSize(340, 0);
			jDialog.setLocationRelativeTo(this);
			jDialog.setVisible(true);
			repaint();
			
		} else if(message.equals("successLogin")) {
			dispose();
		}
	}
	
	public static void main(String[] args) {
		new LoginFrame().setVisible(true);
	}
	
	public void setPassive() {
		userNameJTextField.setEnabled(false);
		passwordJTextField.setEnabled(false);
		loginJButton.setEnabled(false);
		cancelJButton.setEnabled(false);
	}
	
	public void setActive() {
		userNameJTextField.setEnabled(true);
		passwordJTextField.setEnabled(true);
		loginJButton.setEnabled(true);
		cancelJButton.setEnabled(true);
	}
}
