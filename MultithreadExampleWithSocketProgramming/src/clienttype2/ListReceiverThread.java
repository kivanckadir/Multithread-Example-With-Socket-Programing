package clienttype2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import clienttype1.ClientType1;
import job.Job;
import parsers.ClientType1Parser;
import parsers.JobParser;
import parsers.XmlParser;

public class ListReceiverThread extends Thread {
		private ClientType2 clientType2;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		private String message;

		public ListReceiverThread(ClientType2 clientType2) {
			this.clientType2 = clientType2;
			socket = clientType2.getSocket();
			try {
				out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
				in = new Scanner(new BufferedInputStream(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	@Override
	public void run() {

		out.println("<ready></ready>");

		while (true) {

			message = in.nextLine();

			if (message.startsWith("<list>")) {
				
				String listType = XmlParser.get(message, "type");
				
				if (listType.equals("clientType1")) {
					System.out.println("[CLIENT-TYPE1-TEST]" + message);
					clientType2.getClientType2Frame().makeAction("updateMachineTable:" + message);

				} else if (listType.equals("job")) {
					System.out.println("[JOB-TEST]" + message);
					clientType2.getClientType2Frame().makeAction("updateJobTable:" + message);
					
				}
			}
		}
	}
}