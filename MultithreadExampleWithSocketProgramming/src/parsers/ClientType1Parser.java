package parsers;

import java.util.ArrayList;
import java.util.Arrays;

import clienttype1.ClientType1;
import job.Job;

public class ClientType1Parser {
	
	public static ClientType1 getAsObject(String received) {
		ClientType1 clientType1 = new ClientType1();
		int beginIndex, endIndex;

		beginIndex = received.indexOf("id=") + 3;
		endIndex = received.indexOf(", name=");
		int clientId = Integer.parseInt(received.substring(beginIndex, endIndex));
		clientType1.setId(clientId);
		
		
		beginIndex = received.indexOf("name=") + 5;
		endIndex = received.indexOf(", type=");
		String name = received.substring(beginIndex, endIndex);
		clientType1.setName(name);


		beginIndex = received.indexOf("type=") + 5;
		endIndex = received.indexOf(", speed=");
		String type = received.substring(beginIndex, endIndex);
		clientType1.setType(type);


		beginIndex = received.indexOf("speed=") + 6;
		endIndex = received.indexOf(", status=");
		String speed = received.substring(beginIndex, endIndex);
		clientType1.setSpeed(Double.parseDouble(speed));

		
		beginIndex = received.indexOf("status=") + 7;
		endIndex = received.indexOf(", jobs=");
		String status = received.substring(beginIndex, endIndex);
		clientType1.setStatus(status);
		
		beginIndex = received.indexOf("jobs=") + 5;
		endIndex = received.lastIndexOf("]");
		String jobsStr = received.substring(beginIndex, endIndex);
		String jobsArray[] = jobsStr.split("Job");
		ArrayList<Job> jobs = new ArrayList<Job>();
		for (int i = 1; i < jobsArray.length; i++) {
			Job job = JobParser.getAsObject(jobsArray[i]);
			jobs.add(job);
		}
		clientType1.setJobs(jobs);

		return clientType1;
	}
}
