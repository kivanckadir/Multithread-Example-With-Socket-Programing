package clienttype1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import job.Job;

public class IgnoreThread extends Thread {
		
		private ClientType1 clientType1;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		private Job job;
		private String message;

		public IgnoreThread (ClientType1 clientType1, Job job) {
			
			this.clientType1 = clientType1;
			socket = clientType1.getSocket();
			this.job = job;
			
			try {
				out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
				in = new Scanner(new BufferedInputStream(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			out.println("<ignore>" + job.getId() + "</ignore>");
			clientType1.getClientType1Frame().printResult("[IGNORED] " + job.getId() + " id'li ve " + job.getLength() + " kadarlýk " + job.getType() + " tipinde bir iþ reddedildi.\n");
		}
	}
	


	
	