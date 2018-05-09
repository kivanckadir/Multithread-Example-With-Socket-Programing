package server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import clienttype1.ClientType1;
import job.Job;
import lists.Lists;

public class AutoJobGeneratorThread extends Thread {
	
	private PrintWriter output;
	private String types[] = {"CNC", "DÖKÜM", "KILIF", "KAPLAMA"};

	
	public AutoJobGeneratorThread(Socket cSocket) {
		Socket s = cSocket;
		try {
			output = new PrintWriter(new BufferedOutputStream(s.getOutputStream()), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		for (int i = 0; i < 15; i++) {
			Random r = new Random();
			int jobLength = r.nextInt(6000);
			String type = types[r.nextInt(3)];
			
			Lists.jobs.add(i, new Job(i, jobLength, type));
						
			output.println("<job>" + "<id>" + i + "</id>" + "<length>" + jobLength + "</length>" + "<type>"+ type +"</type></job>");
		}
	}	
}
