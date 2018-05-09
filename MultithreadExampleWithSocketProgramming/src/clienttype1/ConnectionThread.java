package clienttype1;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import parsers.XmlParser;

public class ConnectionThread extends Thread {
		
		private ClientType1 clientType1;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		private String message;

		public ConnectionThread (ClientType1 clientType1) {
			
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
			
			//Baðlantý Prosedürü
			if(clientType1.getStatus().equals("NOT CONNECTED")) {

				out.println(clientType1.toString());
				System.out.println(clientType1.toString());
				
				//id Bekle
				if (in.hasNextLine()) {
					message = in.nextLine();
					System.out.println(message);
										
					if(message.startsWith("<id>")) {

						clientType1.setId(Integer.parseInt(XmlParser.get(message, "id")));
						clientType1.setStatus(clientType1.getStatusTypes()[1]);
						System.out.println("ClientType1 id: " + clientType1.getId());
						clientType1.getClientType1Frame().makeAction("updateId:" + clientType1.getId());

						clientType1.setJobReceiverThread(new JobReceiverThread(clientType1));
						clientType1.getJobReceiverThread().start();
					}
				}
			}
		}
	}
		
	