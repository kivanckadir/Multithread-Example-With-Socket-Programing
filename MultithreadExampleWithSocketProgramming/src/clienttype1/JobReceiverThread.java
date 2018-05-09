package clienttype1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JDialog;

import job.Job;
import parsers.XmlParser;

public class JobReceiverThread extends Thread {
		private ClientType1 clientType1;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		private String message;

		public JobReceiverThread(ClientType1 clientType1) {
			this.clientType1 = clientType1;
			socket = clientType1.getSocket();
			try {
				in = new Scanner(new BufferedInputStream(socket.getInputStream()));
				out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	@Override
	public void run() {

		while (true) {
			
			try {
				message = in.nextLine();
			} catch(NoSuchElementException e) {
				JDialog jDialog = new JDialog();
				jDialog.setTitle("Disconnected ");
				jDialog.setResizable(false);
				jDialog.setLocationRelativeTo(clientType1.getClientType1Frame());
				break;
			}

			if (message.startsWith("<job>")) {
				
				int jobId = Integer.parseInt(XmlParser.get(message, "id"));
				double jobLength = Double.parseDouble(XmlParser.get(message, "length"));
				String jobType = XmlParser.get(message, "type");
				
				Job job = new Job(jobId, jobLength, jobType);
				

				if (clientType1.getStatus().equals(clientType1.getStatusTypes()[1])) {
					job.setAccepted(true);
					
					AcceptionThread acceptionThread = new AcceptionThread(clientType1, job);
					acceptionThread.start();
					
					synchronized (this) {
						while (!clientType1.getStatus().equals(clientType1.getStatusTypes()[2])) {
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

				} else {
					job.setAccepted(false);
					
					IgnoreThread ignoreThread = new IgnoreThread(clientType1, job);
					ignoreThread.start();
				}
			}
		}
	}
}