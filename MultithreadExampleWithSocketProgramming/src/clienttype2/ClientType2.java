package clienttype2;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.stream.events.StartDocument;

import clienttype2.ConnectionThread;
import job.Job;
import clienttype1.ClientType1;
import clienttype1.JobReceiverThread;

public class ClientType2 {

	private int id;
	private int port;
	private String userName;
	private String password;
	private String status;

	private ClientType2Frame clientType2Frame;
	private LoginFrame loginFrame;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;

	public ClientType2() {

	}

	public ClientType2(LoginFrame loginFrame) {

		this.loginFrame = loginFrame;

		id = -1;
		port = Integer.parseInt(loginFrame.getPortJTextField().getText());
		userName = loginFrame.getUserNameJTextField().getText();
		password = loginFrame.getPasswordJTextField().getText();
		status = "NOT CONNECTED";
	}

	public ClientType2(ClientType2Frame clientType2Frame) {
		this.clientType2Frame = clientType2Frame;
	}

	public void connect() {

		try {
			socket = new Socket(InetAddress.getLocalHost(), port);
			in = new Scanner(new BufferedInputStream(socket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new ConnectionThread(this).start();
	}

	public void disconnect() {

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getStatus() {
		return status;
	}

	public ClientType2Frame getClientType2Frame() {
		return clientType2Frame;
	}

	public LoginFrame getLoginFrame() {
		return loginFrame;
	}

	public Socket getSocket() {
		return socket;
	}

	public PrintWriter getOut() {
		return out;
	}

	public Scanner getIn() {
		return in;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setClientType2Frame(ClientType2Frame clientType2Frame) {
		this.clientType2Frame = clientType2Frame;
	}

	public void setLoginFrame(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public void setIn(Scanner in) {
		this.in = in;
	}

	@Override
	public String toString() {
		return "ClientType2 [id=" + id + ", userName=" + userName + ", password=" + password + ", status=" + status
				+ "]";
	}
}
