package server;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.table.TableColumn;

import clienttype1.ClientType1;
import clienttype2.ClientType2;
import job.Job;
import lists.Lists;
import parsers.XmlParser;

public class Server extends Thread {

	private ServerFrame mServerFrame;
	private ServerSocket serverSocket;
	private int mPort;
	private Socket client;
	private JobRouterThread jobRouterThread;
	private int c1id = 0;
	private int c2id = 0;
	private int jobid = 0;

	public Server(ServerFrame serverFrame) {
		mServerFrame = serverFrame;
		mPort = Integer.parseInt(serverFrame.getPortJTextField().getText());

	}

	public void startServer() throws IOException {
		serverSocket = new ServerSocket(mPort);
		start();
	}

	public void stopServer() throws IOException {
		serverSocket.close();
		stop();
	}

	@Override
	public void run() {
		jobRouterThread = new JobRouterThread(this);
		jobRouterThread.start();

		try {
			do {

				client = serverSocket.accept();
				System.out.println("\nNew client accepted.\n");
				ClientHandler handler = new ClientHandler(client);
				handler.start();

			} while (true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ClientHandler extends Thread {

		private Socket client;
		private Scanner input;
		private PrintWriter output;

		public ClientHandler(Socket socket) {

			client = socket;

			try {
				input = new Scanner(new BufferedInputStream(client.getInputStream()));
				output = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
		}

		public void run() {
			String received = null;
			ClientType1 clientType1 = new ClientType1();
			ClientType2 clientType2 = new ClientType2();
			String clientType = null;

			do {
				try {
					received = input.nextLine();
				} catch (NoSuchElementException e) {
					break;
				}

				if (received.equals("QUIT")) {
					break;

				} else {

					if (received.startsWith("ClientType1")) {
						clientType = "clientType1";
						int beginIndex, endIndex;

						beginIndex = received.indexOf("id=") + 3;
						endIndex = received.indexOf(", name=");
						int index = Integer.parseInt(received.substring(beginIndex, endIndex));

						beginIndex = received.indexOf("name=") + 5;
						endIndex = received.indexOf(", type=");
						String name = received.substring(beginIndex, endIndex);

						beginIndex = received.indexOf("type=") + 5;
						endIndex = received.indexOf(", speed=");
						String type = received.substring(beginIndex, endIndex);

						beginIndex = received.indexOf("speed=") + 6;
						endIndex = received.indexOf(", status=");
						String speed = received.substring(beginIndex, endIndex);

						beginIndex = received.indexOf("status=") + 7;
						endIndex = received.indexOf(", jobs=");
						String status = received.substring(beginIndex, endIndex);

						ArrayList<Job> jobs = new ArrayList<Job>();

						clientType1.setId(index);
						clientType1.setName(name);
						clientType1.setType(type);
						clientType1.setSpeed(Double.parseDouble(speed));
						clientType1.setStatus(status);
						clientType1.setJobs(jobs);
						clientType1.setSocket(client);

						if (status.equals("NOT CONNECTED")) {

							// Listeye ekle, listedeki indexi id belirle, id bilgisini ilet, listedeki
							// statusu ve soketi güncelle.
							incrementC1id();
							status = "EMPTY";

							clientType1.setId(c1id);
							clientType1.setStatus(status);

							Lists.clientType1Map.put(clientType1.getId(), clientType1);

							output.println("<id>" + clientType1.getId() + "</id>");
							mServerFrame.printResult(
									"[CONNECTED-CLIENT-TYPE1] " + clientType1.toString() + "\n");

							updateAllMachineTables();

							synchronized (jobRouterThread) {
								jobRouterThread.notifyAll();
							}
						}
					}

					else if (received.startsWith("<accept>")) {
						int jobId = Integer.parseInt(XmlParser.get(received, "accept"));
						double jobLength = Lists.jobMap.get(jobId).getLength();
						String jobType = Lists.jobMap.get(jobId).getType();

						Job job = new Job(jobId, jobLength, jobType);
						job.setAccepted(true);
						mServerFrame.printResult("[ACCEPTED] " + job.getId() + " id'li " + job.getLength()
								+ " kadarlýk " + job.getType() + " tipinde bir iþ " + clientType1.getId()
								+ " id'li makine tarafýndan kabul edildi.\n");

						synchronized (jobRouterThread) {
							Lists.jobMap.put(job.getId(), job);
							jobRouterThread.notifyAll();
						}

						updateAllJobTables();
					}

					else if (received.startsWith("<ignore>")) {
						int jobId = Integer.parseInt(XmlParser.get(received, "ignore"));
						double jobLength = Lists.jobMap.get(jobId).getLength();
						String jobType = Lists.jobMap.get(jobId).getType();

						Job job = new Job(jobId, jobLength, jobType);
						job.setAccepted(false);
						mServerFrame.printResult("[IGNORED] " + job.getId() + " id'li " + job.getLength() + " kadarlýk "
								+ job.getType() + " tipinde bir iþ " + clientType1.getId()
								+ " id'li makine tarafýndan reddedildi.\n");

						synchronized (jobRouterThread) {
							Lists.jobMap.put(job.getId(), job);
							jobRouterThread.notifyAll();
						}
					}

					else if (received.startsWith("<status>")) {
						mServerFrame
								.printResult("[CHANGED] " + clientType1.getId() + " id'li makinenin statusü deðiþti: "
										+ clientType1.getStatus() + "->" + XmlParser.get(received, "status") + "\n");

						// Status'ü güncelle
						clientType1.setStatus(XmlParser.get(received, "status"));
						Lists.clientType1Map.put(clientType1.getId(), clientType1);

						updateAllMachineTables();
					}

					else if (received.startsWith("<complete>")) {
						int jobId = Integer.parseInt(XmlParser.get(received, "complete"));
						double jobLength = Lists.jobMap.get(jobId).getLength();
						String jobType = Lists.jobMap.get(jobId).getType();

						Job job = new Job(jobId, jobLength, jobType);
						job.setAccepted(true);

						mServerFrame.printResult("[COMPLETED] " + job.getId() + " id'li " + job.getLength()
								+ " kadarlýk " + job.getType() + " tipinde bir iþ " + clientType1.getId()
								+ " id'li makine tarafýndan tamamlandý.\n");

						// Counter'larý güncelle (Tamamlanan iþleri güncelle)
						clientType1.getJobs().add(job);

						Lists.jobMap.remove(job.getId());
						Lists.clientType1Map.put(clientType1.getId(), clientType1);

						mServerFrame.printResult("[CLIENT-TYPE1-INFO] " + clientType1.getId()
								+ " id'li makinenin toplam çalýþmasý: " + clientType1.getTotalJobTime() + "\n");
						mServerFrame.printResult("[CLIENT-TYPE1-INFO] " + clientType1.getId()
								+ " id'li makinenin yaptýðý iþler: " + clientType1.getJobs() + "\n");

						updateAllMachineTables();
					}

					/*******************************************************************************************************************************************************************************************************************************************/

					else if (received.startsWith("ClientType2")) {
						clientType = "clientType2";
						int beginIndex, endIndex;

						beginIndex = received.indexOf("id=") + 3;
						endIndex = received.indexOf(", userName=");
						int clientId = Integer.parseInt(received.substring(beginIndex, endIndex));

						beginIndex = received.indexOf("userName=") + 9;
						endIndex = received.indexOf(", password=");
						String userName = received.substring(beginIndex, endIndex);

						beginIndex = received.indexOf("password=") + 9;
						endIndex = received.indexOf(", status=");
						String password = received.substring(beginIndex, endIndex);

						beginIndex = received.indexOf("status=") + 7;
						endIndex = received.indexOf("]");
						String status = received.substring(beginIndex, endIndex);

						clientType2.setId(clientId);
						clientType2.setUserName(userName);
						clientType2.setPassword(password);
						clientType2.setStatus(status);
						clientType2.setSocket(client);

						if (Lists.usernamePassword.containsKey(userName)
								&& Lists.usernamePassword.get(userName).equals(password)) {

							if (Lists.activeUsers.containsKey(userName)
									&& Lists.activeUsers.get(userName).equals(password)) {
								output.println("<login>inUse</login>");
								break;

							} else if (status.equals("NOT CONNECTED")) {

								// Listeye ekle, listedeki indexi id belirle, id bilgisini ilet, statusu
								// güncelle.
								incrementC2id();
								status = "CONNECTED";

								clientType2.setId(c2id);
								clientType2.setStatus(status);

								Lists.clientType2Map.put(clientType2.getId(), clientType2);
								Lists.activeUsers.put(clientType2.getUserName(), clientType2.getPassword());

								output.println("<id>" + clientType2.getId() + "</id>");

								mServerFrame.printResult(
										"[CONNECTED-CLIENT-TYPE2] " + clientType2.toString() + "\n");
							}

						} else {
							output.println("<login>false</login>");
							break;
						}
					} else if (received.startsWith("<job>")) {
						incrementJobId();
						String jobType = XmlParser.get(received, "type");
						double jobLength = Double.parseDouble(XmlParser.get(received, "length"));

						Job job = new Job(jobid, jobLength, jobType);

						synchronized (jobRouterThread) {
							Lists.jobMap.put(job.getId(), job);
							jobRouterThread.notifyAll();
						}

						updateAllJobTables();

					} else if (received.startsWith("<ready>")) {
						updateTablesForAClient(clientType2.getSocket());
					}
				}
			} while (!received.equals("QUIT"));

			try {
				if (client != null) {
					if (clientType.equals("clientType1")) {
						client.close();

						clientType1.setStatus("NOT CONNECTED");
						clientType1.setSocket(client);

						Lists.clientType1Map.put(clientType1.getId(), clientType1);
						Lists.clientType1Map.remove(clientType1.getId());

						mServerFrame.printResult("[DISCONNECT-CLIENT-TYPE1] " + clientType1.toString() + "\n");
						updateAllMachineTables();

					} else if (clientType.equals("clientType2")) {
						if(clientType2.getId()==-1) {
							client.close();
							mServerFrame.printResult("[CONNECTION-ERROR] " + clientType2.toString() + "\n");
						} else {
							client.close();

							clientType2.setStatus("NOT CONNECTED");
							clientType2.setSocket(client);

							Lists.clientType2Map.put(clientType2.getId(), clientType2);
							Lists.clientType2Map.remove(clientType2.getId());

							Lists.activeUsers.remove(clientType2.getUserName(), clientType2.getPassword());

							mServerFrame.printResult("[DISCONNECT-CLIENT-TYPE2] " + clientType2.toString() + "\n");
						}
						
					}

				}
			} catch (IOException ioEx) {
				System.out.println("Unable to disconnect!");
			}
		}
	}

	private void updateAllMachineTables() {

		for (int i = 0; i < getConectedClientType2List().size(); i++) {

			try {
				new PrintWriter(
						new BufferedOutputStream(getConectedClientType2List().get(i).getSocket().getOutputStream()),
						true).println(
								"<list><type>clientType1</type><items>" + getConectedClientType1List().toString()
										+ "</items></list>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateAllJobTables() {

		for (int i = 0; i < getConectedClientType2List().size(); i++) {
			try {
				new PrintWriter(
						new BufferedOutputStream(getConectedClientType2List().get(i).getSocket().getOutputStream()),
						true).println("<list><type>job</type><items>" + getPendingJobs() + "</items></list>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateTablesForAClient(Socket client) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println("<list><type>clientType1</type><items>" + getConectedClientType1List() + "</items></list>");
		out.println("<list><type>job</type><items>" + getPendingJobs() + "</items></list>");
	}

	public ArrayList<ClientType1> getConectedClientType1List() {
		ArrayList<ClientType1> connectedClientType1List = new ArrayList<ClientType1>();
		for (int key : Lists.clientType1Map.keySet()) {
			connectedClientType1List.add(Lists.clientType1Map.get(key));
		}
		return connectedClientType1List;
	}

	public ArrayList<ClientType2> getConectedClientType2List() {
		ArrayList<ClientType2> connectedClientType2List = new ArrayList<ClientType2>();
		for (int key : Lists.clientType2Map.keySet()) {
			connectedClientType2List.add(Lists.clientType2Map.get(key));
		}
		return connectedClientType2List;
	}

	public ArrayList<Job> getPendingJobs() {
		ArrayList<Job> pendingJobs = new ArrayList<Job>();

		for (int key : Lists.jobMap.keySet()) {
			if (!Lists.jobMap.get(key).isAccepted()) {
				pendingJobs.add(Lists.jobMap.get(key));
			}
		}
		return pendingJobs;
	}

	public synchronized void incrementJobId() {
		jobid++;
	}

	public synchronized void incrementC1id() {
		c1id++;
	}

	public synchronized void incrementC2id() {
		c2id++;
	}
}

// package server;
//
// import java.awt.List;
// import java.io.BufferedInputStream;
// import java.io.BufferedOutputStream;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.PrintWriter;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.Map;
// import java.util.NoSuchElementException;
// import java.util.Random;
// import java.util.Scanner;
//
// import javax.swing.JFrame;
// import javax.swing.table.TableColumn;
//
// import clienttype1.ClientType1;
// import clienttype2.ClientType2;
// import job.Job;
// import lists.Lists;
// import parsers.XmlParser;
//
// public class Server extends Thread {
//
// private ServerFrame mServerFrame;
// private ServerSocket serverSocket;
// private int mPort;
// private Socket client;
// private JobRouterThread jobRouterThread;
// private int c1id = 0;
// private int c2id = 0;
//
// public Server(ServerFrame serverFrame) {
// mServerFrame = serverFrame;
// mPort = Integer.parseInt(serverFrame.getPortJTextField().getText());
//
// }
//
// public void startServer() throws IOException {
// serverSocket = new ServerSocket(mPort);
// start();
// }
//
// public void stopServer() throws IOException {
// serverSocket.close();
// stop();
// }
//
// @Override
// public void run() {
// jobRouterThread = new JobRouterThread(this);
// jobRouterThread.start();
//
// try {
// do {
//
// client = serverSocket.accept();
// System.out.println("\nNew client accepted.\n");
// ClientHandler handler = new ClientHandler(client);
// handler.start();
//
// } while (true);
//
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
//
// class ClientHandler extends Thread {
//
// private Socket client;
// private Scanner input;
// private PrintWriter output;
//
// public ClientHandler(Socket socket) {
//
// client = socket;
//
// try {
// input = new Scanner(new BufferedInputStream(client.getInputStream()));
// output = new PrintWriter(new BufferedOutputStream(client.getOutputStream()),
// true);
// } catch (IOException ioEx) {
// ioEx.printStackTrace();
// }
// }
//
// public void run() {
// String received = null;
// ClientType1 clientType1 = new ClientType1();
// ClientType2 clientType2 = new ClientType2();
// String clientType = null;
//
// do {
// try {
// received = input.nextLine();
// } catch (NoSuchElementException e) {
// break;
// }
//
// if (received.equals("QUIT")) {
// break;
//
// } else {
//
// if (received.startsWith("ClientType1")) {
// clientType = "clientType1";
// int beginIndex, endIndex;
//
// beginIndex = received.indexOf("id=") + 3;
// endIndex = received.indexOf(", name=");
// int index = Integer.parseInt(received.substring(beginIndex, endIndex));
//
// beginIndex = received.indexOf("name=") + 5;
// endIndex = received.indexOf(", type=");
// String name = received.substring(beginIndex, endIndex);
//
// beginIndex = received.indexOf("type=") + 5;
// endIndex = received.indexOf(", speed=");
// String type = received.substring(beginIndex, endIndex);
//
// beginIndex = received.indexOf("speed=") + 6;
// endIndex = received.indexOf(", status=");
// String speed = received.substring(beginIndex, endIndex);
//
// beginIndex = received.indexOf("status=") + 7;
// endIndex = received.indexOf(", jobs=");
// String status = received.substring(beginIndex, endIndex);
//
// ArrayList<Job> jobs = new ArrayList<Job>();
//
// clientType1.setId(index);
// clientType1.setName(name);
// clientType1.setType(type);
// clientType1.setSpeed(Double.parseDouble(speed));
// clientType1.setStatus(status);
// clientType1.setJobs(jobs);
// clientType1.setSocket(client);
//
// if (status.equals("NOT CONNECTED")) {
//
// //Listeye ekle, listedeki indexi id belirle, id bilgisini ilet, listedeki
// statusu ve soketi güncelle.
// Lists.clientType1List.add(clientType1);
// index = Lists.clientType1List.indexOf(clientType1);
// status = "EMPTY";
//
// clientType1.setId(index);
// clientType1.setStatus(status);
//
// Lists.clientType1List.get(index).setId(index);
// Lists.clientType1List.get(index).setStatus(status);
//
// output.println("<id>" + index + "</id>");
// mServerFrame.printResult("ClientType1 tipinde bir istemci baðlandý. ("+
// clientType1.toString() +")\n");
//
// updateAllMachineTables();
//
// synchronized (jobRouterThread) {
// jobRouterThread.notifyAll();
// }
// }
// }
//
// else if (received.startsWith("<accept>")) {
// int jobId = Integer.parseInt(XmlParser.get(received, "accept"));
// double jobLength = Lists.jobs.get(jobId).getLength();
// String jobType = Lists.jobs.get(jobId).getType();
//
// Job job = new Job(jobId, jobLength, jobType);
// job.setAccepted(true);
// mServerFrame.printResult("[ACCEPTED] " + job.getId() + " id'li " +
// job.getLength() + " kadarlýk " + job.getType() + " tipinde bir iþ " +
// clientType1.getId() + " id'li makine tarafýndan kabul edildi.\n");
//
// synchronized (jobRouterThread) {
// Lists.jobs.get(jobId).setAccepted(true); //Ýþi kabul edildi olarak iþaretle
// jobRouterThread.notifyAll();
// }
//
// updateAllJobTables();
// }
//
// else if (received.startsWith("<ignore>")) {
// int jobId = Integer.parseInt(XmlParser.get(received, "ignore"));
// double jobLength = Lists.jobs.get(jobId).getLength();
// String jobType = Lists.jobs.get(jobId).getType();
//
// Job job = new Job(jobId, jobLength, jobType);
// job.setAccepted(false);
// mServerFrame.printResult("[IGNORED] " + job.getId() + " id'li " +
// job.getLength() + " kadarlýk " + job.getType() + " tipinde bir iþ " +
// clientType1.getId() + " id'li makine tarafýndan reddedildi.\n");
//
// synchronized (jobRouterThread) {
// Lists.jobs.get(jobId).setAccepted(false); //Ýþi kabul edilmedi olarak
// iþaretle
// jobRouterThread.notifyAll();
// }
// }
//
// else if (received.startsWith("<status>")) {
// mServerFrame.printResult("[CHANGED] " +
// Lists.clientType1List.get(clientType1.getId()).getId() + " id'li makinenin
// statusü deðiþti: " +
// Lists.clientType1List.get(clientType1.getId()).getStatus() + "->" +
// XmlParser.get(received, "status") + "\n");
//
// //Status'ü güncelle
// clientType1.setStatus(XmlParser.get(received, "status"));
//
// Lists.clientType1List.get(clientType1.getId()).setStatus(clientType1.getStatus());
//
// updateAllMachineTables();
// }
//
// else if (received.startsWith("<complete>")) {
// int jobId = Integer.parseInt(XmlParser.get(received, "complete"));
// double jobLength = Lists.jobs.get(jobId).getLength();
// String jobType = Lists.jobs.get(jobId).getType();
//
// Job job = new Job(jobId, jobLength, jobType);
// job.setAccepted(true);
//
// mServerFrame.printResult("[COMPLETED] " + job.getId() + " id'li " +
// job.getLength() + " kadarlýk " + job.getType() + " tipinde bir iþ " +
// clientType1.getId() + " id'li makine tarafýndan tamamlandý.\n");
//
// //Counter'larý güncelle (Tamamlanan iþleri güncelle)
// clientType1.getJobs().add(job);
//
// Lists.clientType1List.get(clientType1.getId()).setJobs(clientType1.getJobs());
//
// mServerFrame.printResult("[CLIENT-INFO] " + clientType1.getId() + " id'li
// makinenin toplam çalýþmasý: " +
// Lists.clientType1List.get(clientType1.getId()).getTotalJobTime() + "\n");
// mServerFrame.printResult("[CLIENT-INFO] " + clientType1.getId() + " id'li
// makinenin yaptýðý iþler: " +
// Lists.clientType1List.get(clientType1.getId()).getJobs() + "\n");
//
// updateAllMachineTables();
// }
//
//
/// *******************************************************************************************************************************************************************************************************************************************/
//
//
// else if (received.startsWith("ClientType2")) {
// clientType = "clientType2";
// int beginIndex, endIndex;
//
// beginIndex = received.indexOf("id=") + 3;
// endIndex = received.indexOf(", userName=");
// int clientId = Integer.parseInt(received.substring(beginIndex, endIndex));
//
// beginIndex = received.indexOf("userName=") + 9;
// endIndex = received.indexOf(", password=");
// String userName = received.substring(beginIndex, endIndex);
//
// beginIndex = received.indexOf("password=") + 9;
// endIndex = received.indexOf(", status=");
// String password = received.substring(beginIndex, endIndex);
//
// beginIndex = received.indexOf("status=") + 7;
// endIndex = received.indexOf("]");
// String status = received.substring(beginIndex, endIndex);
//
// clientType2.setId(clientId);
// clientType2.setUserName(userName);
// clientType2.setPassword(password);
// clientType2.setStatus(status);
// clientType2.setSocket(client);
//
// if (Lists.usernamePassword.containsKey(userName) &&
// Lists.usernamePassword.get(userName).equals(password)) {
//
//
// if(Lists.activeUsers.containsKey(userName) &&
// Lists.activeUsers.get(userName).equals(password)) {
// output.println("<login>inUse</login>");
//
// } else if (status.equals("NOT CONNECTED")) {
//
// //Listeye ekle, listedeki indexi id belirle, id bilgisini ilet, statusu
// güncelle.
// Lists.clientType2List.add(clientType2);
// clientId = Lists.clientType2List.indexOf(clientType2);
// status = "CONNECTED";
//
// clientType2.setId(clientId);
// clientType2.setId(clientId);
//
// Lists.clientType2List.get(clientId).setId(clientId);
// Lists.clientType2List.get(clientId).setStatus(status);
//
// Lists.activeUsers.put(clientType2.getUserName(), clientType2.getPassword());
//
// output.println("<id>" + clientId + "</id>");
//
// mServerFrame.printResult("ClientType2 tipinde bir istemci baðlandý. ("+
// clientType2.toString() +")\n");
// }
//
// } else {
// output.println("<login>false</login>");
// }
// } else if (received.startsWith("<job>")) {
// int jobId = -1;
// String jobType = XmlParser.get(received, "type");
// double jobLength = Double.parseDouble(XmlParser.get(received, "length"));
//
// Job job = new Job(jobId, jobLength, jobType);
//
// synchronized (jobRouterThread) {
// Lists.jobs.add(job);
// jobId = Lists.jobs.indexOf(job);
//
// job.setId(jobId);
//
// Lists.jobs.get(jobId).setId(jobId);
//
// jobRouterThread.notifyAll();
// }
//
// updateAllJobTables();
//
// } else if (received.startsWith("<ready>")) {
// updateTablesForAClient(clientType2.getSocket());
// }
// }
// } while (!received.equals("QUIT"));
//
// try {
// if (client != null) {
// if (clientType.equals("clientType1")) {
// client.close();
//
// clientType1.setStatus("NOT CONNECTED");
// clientType1.setSocket(client);
//
// Lists.clientType1List.get(clientType1.getId()).setStatus("NOT CONNECTED");
// Lists.clientType2List.get(clientType1.getId()).setSocket(client);
//
// Lists.clientType2List.remove(clientType1);
//
// mServerFrame.printResult("[DISCONNECT-CLIENT-TYPE1] " +
// clientType1.toString() + "\n");
// updateAllMachineTables();
//
//
// } else if(clientType.equals("clientType2")) {
// client.close();
//
// clientType2.setStatus("NOT CONNECTED");
// clientType2.setSocket(client);
//
// Lists.clientType2List.get(clientType2.getId()).setStatus("NOT CONNECTED");
// Lists.clientType2List.get(clientType2.getId()).setSocket(client);
//
// Lists.clientType2List.remove(clientType2);
// Lists.activeUsers.remove(clientType2.getUserName(),
// clientType2.getPassword());
//
// mServerFrame.printResult("[DISCONNECT-CLIENT-TYPE2] " +
// clientType2.toString() + "\n");
//
// }
//
// }
// } catch (IOException ioEx) {
// System.out.println("Unable to disconnect!");
// }
// }
// }
//
// private void updateAllMachineTables() {
//
// for (int i = 0; i < getConectedClientType2List().size(); i++) {
//
// try {
// new PrintWriter(new
// BufferedOutputStream(getConectedClientType2List().get(i).getSocket().getOutputStream()),
// true).println("<list><type>clientType1</type><items>"+
// getConectedClientType1List().toString() +"</items></list>");
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }
//
// private void updateAllJobTables() {
//
// for (int i = 0; i < getConectedClientType2List().size(); i++) {
// try {
// new PrintWriter(new
// BufferedOutputStream(getConectedClientType2List().get(i).getSocket().getOutputStream()),
// true).println("<list><type>job</type><items>"+ getPendingJobs()
// +"</items></list>");
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }
//
// private void updateTablesForAClient(Socket client) {
// PrintWriter out = null;
// try {
// out = new PrintWriter(new BufferedOutputStream(client.getOutputStream()),
// true);
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// out.println("<list><type>clientType1</type><items>"+
// getConectedClientType1List() +"</items></list>");
// out.println("<list><type>job</type><items>"+ getPendingJobs()
// +"</items></list>");
// }
//
// public ArrayList<ClientType1> getConectedClientType1List(){
// ArrayList<ClientType1> connectedClientType1List = new
// ArrayList<ClientType1>();
// for (int i = 0; i < Lists.clientType1List.size(); i++) {
// if (!Lists.clientType2List.get(i).getStatus().equals("NOT CONNECTED") &&
// !(Lists.clientType1List.get(i).getSocket().isClosed())) {
// connectedClientType1List.add(Lists.clientType1List.get(i));
// }
// }
// return connectedClientType1List;
// }
//
// public ArrayList<ClientType2> getConectedClientType2List(){
// ArrayList<ClientType2> connectedClientType2List = new
// ArrayList<ClientType2>();
// for (int i = 0; i < Lists.clientType2List.size(); i++) {
// if (!Lists.clientType2List.get(i).getStatus().equals("NOT CONNECTED") &&
// !(Lists.clientType2List.get(i).getSocket().isClosed())) {
// connectedClientType2List.add(Lists.clientType2List.get(i));
// }
// }
// return connectedClientType2List;
// }
//
// public ArrayList<Job> getPendingJobs(){
// ArrayList<Job> pendingJobs = new ArrayList<Job>();
//
// for (int i = 0; i < Lists.jobs.size(); i++) {
// if (Lists.jobs.get(i).isAccepted() == false) {
// pendingJobs.add(Lists.jobs.get(i));
// }
// }
// return pendingJobs;
// }
// }
