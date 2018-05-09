package clienttype1;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.stream.events.StartDocument;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

import job.Job;


public class ClientType1 {

	private String speeds[] = { "0.5x", "1x", "1.5x", "2x" };
	private String statusTypes[] = { "NOT CONNECTED", "EMPTY", "BUSY" };

	private ClientType1Frame clientType1Frame;
	
	private int id;
	private String name;
	private String type;
	private double speed;
	private String status;
	private ArrayList<Job> jobs;
	
	private Socket socket;
	private int port;
	private Scanner in;
	private PrintWriter out;
	
	private ConnectionThread connectionThread;
	private JobReceiverThread jobReceiverThread;
	
	public ClientType1() {

	}

	public ClientType1(ClientType1Frame clientType1Frame) {

		this.clientType1Frame = clientType1Frame;

		id = -1;
		port = Integer.parseInt(clientType1Frame.getPortJTextField().getText());
		name = clientType1Frame.getNameJTextField().getText();
		type = clientType1Frame.getTypeJCombobox().getSelectedItem().toString();
		String speedStr = clientType1Frame.getSpeedJCombobox().getSelectedItem().toString();
		jobs = new ArrayList<Job>();

		if (speedStr.equals(speeds[0])) {
			speed =  0.5;
		} else if (speedStr.equals(speeds[1])) {
			speed = 1.0;
		} else if (speedStr.equals(speeds[2])) {
			speed = 1.5;
		} else if (speedStr.equals(speeds[3])) {
			speed = 2.0;
		}

		status = statusTypes[0];
	}
	
	public void connect() {		
		try {
			socket = new Socket(InetAddress.getLocalHost(), port);
			in = new Scanner(new BufferedInputStream(socket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		connectionThread = new ConnectionThread(this);
		connectionThread.start();

	}
	
	public void work(Job job) {

		synchronized (jobReceiverThread) {
			status = statusTypes[2];
			clientType1Frame.makeAction("updateStatus:" + status);
			out.println("<status>" + status + "</status>");
			jobReceiverThread.notifyAll();

		}

		try {
			Thread.sleep((long) (job.getLength()/speed));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		status = statusTypes[1];
		clientType1Frame.makeAction("updateStatus:" + status);
		clientType1Frame.printResult("[COMPLETED] " + job.getId() +" idli ve "+ job.getLength() + " kadarlýk " + job.getType() + " türünde bir iþ bitirildi.\n");
		jobs.add(job);
		clientType1Frame.makeAction("updateCounters:"+jobs.size()+":"+getTotalJobTime()+":"+getTotalWorkTime());
		
		out.println("<status>" + status + "</status>");
		out.println("<complete>" + job.getId() + "</complete>");
	}
	
	public int getTotalJobCount() {
		return jobs.size();
	}
	
	public int getTotalWorkTime() {
		int sum = 0;
		sum = (int) (sum + getTotalJobTime()/speed);
		return sum;
	}
	
	public double getTotalJobTime() {	
		
		double sum = 0;
		for (int i = 0; i < jobs.size(); i++) {
			sum = sum + jobs.get(i).getLength();
		}
		return sum;
	}
	

	public void disconnect() {

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] getSpeeds() {
		return speeds;
	}

	public String[] getStatusTypes() {
		return statusTypes;
	}

	public ClientType1Frame getClientType1Frame() {
		return clientType1Frame;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public double getSpeed() {
		return speed;
	}

	public String getStatus() {
		return status;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public Socket getSocket() {
		return socket;
	}

	public int getPort() {
		return port;
	}

	public Scanner getIn() {
		return in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setSpeeds(String[] speeds) {
		this.speeds = speeds;
	}

	public void setStatusTypes(String[] statusTypes) {
		this.statusTypes = statusTypes;
	}

	public void setClientType1Frame(ClientType1Frame clientType1Frame) {
		this.clientType1Frame = clientType1Frame;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSpeed(double d) {
		this.speed = d;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setJobs(ArrayList<Job> jobs) {
		this.jobs = jobs;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIn(Scanner in) {
		this.in = in;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public ConnectionThread getConnectionThread() {
		return connectionThread;
	}

	public JobReceiverThread getJobReceiverThread() {
		return jobReceiverThread;
	}

	public void setConnectionThread(ConnectionThread connectionThread) {
		this.connectionThread = connectionThread;
	}

	public void setJobReceiverThread(JobReceiverThread jobReceiverThread) {
		this.jobReceiverThread = jobReceiverThread;
	}

	@Override
	public String toString() {
		return "ClientType1 [id=" + id + ", name=" + name + ", type=" + type + ", speed=" + speed + ", status=" + status
				+ ", jobs=" + jobs + "]";
	}

}
