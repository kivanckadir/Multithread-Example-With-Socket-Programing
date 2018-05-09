package clienttype2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import clienttype1.ClientType1;

public class JobSenderThread extends Thread{

	private ClientType2 clientType2;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	
	private String jobLength;
	private String jobType;
	
	public JobSenderThread(ClientType2 clientType2, String jobLength, String jobType) {
		
		this.clientType2 = clientType2;
		socket = clientType2.getSocket();
		
		try {
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			in = new Scanner(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.jobLength = jobLength;
		this.jobType = jobType;
	}
	
	@Override
	public void run() {
		super.run();

		out.println("<job><length>"+jobLength+"</length><type>"+jobType+"</type></job>");
	}
}
