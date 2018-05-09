package clienttype2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import clienttype1.JobReceiverThread;
import parsers.XmlParser;

public class ConnectionThread extends Thread {

	private ClientType2 clientType2;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private String message;

	public ConnectionThread(ClientType2 clientType2) {

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

		// Baðlantý Prosedürü
		if (clientType2.getStatus().equals("NOT CONNECTED")) {
			out.println(clientType2.toString());

			// id Bekle
			if (in.hasNextLine()) {
				message = in.nextLine();

				if (message.startsWith("<login>")) {

					if (XmlParser.get(message, "login").equals("false")) {
						clientType2.getLoginFrame().makeAction("loginError");
					} else if (XmlParser.get(message, "login").equals("inUse")) {
						clientType2.getLoginFrame().makeAction("inUse");
					}

				} else if (message.startsWith("<id>")) {

					clientType2.setId(Integer.parseInt(XmlParser.get(message, "id")));
					clientType2.setStatus("CONNECTED");
					System.out.println("ClientType2 id: " + clientType2.getId());
					clientType2.getLoginFrame().makeAction("successLogin");
					
					ClientType2Frame clientType2Frame = new ClientType2Frame(clientType2);
					clientType2.setClientType2Frame(clientType2Frame);
					clientType2Frame.makeAction("updateId:" + clientType2.getId());
					clientType2Frame.makeAction("updateStatus:" + clientType2.getStatus());

					clientType2Frame.setVisible(true);
					
					new ListReceiverThread(clientType2).start();
				}
			}
		}
	}

}
