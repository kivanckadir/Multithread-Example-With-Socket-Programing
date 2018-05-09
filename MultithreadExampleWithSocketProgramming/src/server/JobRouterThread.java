package server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import clienttype1.ClientType1;
import job.Job;
import lists.Lists;

public class JobRouterThread extends Thread {

	private Server server;
	private PrintWriter out;

	public JobRouterThread(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		super.run();

		while (true) {
			synchronized (this) {
				while (Lists.jobMap.isEmpty() || Lists.clientType1Map.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		
			
			for (int jobKey : Lists.jobMap.keySet()) {
				
				Job job = Lists.jobMap.get(jobKey);
				if(job!=null && !job.isAccepted()) {
					for (int clientType1Key : Lists.clientType1Map.keySet()) {
						ClientType1 clientType1 = Lists.clientType1Map.get(clientType1Key);
						if (clientType1 !=null && clientType1.getType().equals(job.getType())) {
							try {
								out = new PrintWriter(new BufferedOutputStream(clientType1.getSocket().getOutputStream()),
										true);
								out.println("<job>" + "<id>" + job.getId() + "</id>" + "<length>" + job.getLength()
										+ "</length>" + "<type>" + job.getType() + "</type></job>");

								synchronized (this) {
									wait();

									if (Lists.jobMap.get(jobKey).isAccepted()) {
										break;
									} else if (!Lists.jobMap.get(jobKey).isAccepted()) {
										Thread.sleep(800);
									}
								}

							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}



//package server;
//
//import java.io.BufferedOutputStream;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//import clienttype1.ClientType1;
//import job.Job;
//import lists.Lists;
//
//public class JobRouterThread extends Thread {
//
//	private Server server;
//	private PrintWriter out;
//
//	public JobRouterThread(Server server) {
//		this.server = server;
//	}
//
//	@Override
//	public void run() {
//		super.run();
//
//		while (true) {
//			synchronized (this) {
//				while (!(Lists.jobs.size() > 0) || !(Lists.clientType1List.size() > 0)) {
//					try {
//						wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			for (int i = 0; i < Lists.jobs.size(); i++) {
//				if (Lists.jobs.get(i).isAccepted() == false) {
//					Job job = Lists.jobs.get(i);
//					for (int j = 0; j < Lists.clientType1List.size(); j++) {
//						if (Lists.clientType1List.get(j).getType().equals(Lists.jobs.get(i).getType()) && !Lists.clientType1List.get(j).getStatus().equals("NOT CONNECTED")) {
//							ClientType1 clientType1 = Lists.clientType1List.get(j);
//							try {
//								out = new PrintWriter(new BufferedOutputStream(clientType1.getSocket().getOutputStream()), true);
//								out.println("<job>" + "<id>" + job.getId() + "</id>" + "<length>" + job.getLength() + "</length>" + "<type>" + job.getType() + "</type></job>");
//
//								synchronized (this) {
//									wait();
//
//									if (Lists.jobs.get(i).isAccepted()) {
//										break;
//									} else if (!Lists.jobs.get(i).isAccepted()) {
//										Thread.sleep(500);
//									}
//								}
//
//							} catch (IOException e) {
//								e.printStackTrace();
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//}
//
//
//
//
//
//
//
//
//
