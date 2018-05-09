package parsers;

import clienttype1.ClientType1;
import job.Job;

public class JobParser {
	
	public static Job getAsObject(String received) {
		Job job = new Job();
		int beginIndex, endIndex;

		beginIndex = received.indexOf("id=") + 3;
		endIndex = received.indexOf(", length=");
		int jobId = Integer.parseInt(received.substring(beginIndex, endIndex));
		job.setId(jobId);
		
		
		beginIndex = received.indexOf("length=") + 7;
		endIndex = received.indexOf(", type=");
		double jobLength = Double.parseDouble(received.substring(beginIndex, endIndex));
		job.setLength(jobLength);


		beginIndex = received.indexOf("type=") + 5;
		endIndex = received.indexOf(", isAccepted=");
		String type = received.substring(beginIndex, endIndex);
		job.setType(type);


		beginIndex = received.indexOf("isAccepted=") + 11;
		endIndex = received.indexOf("]");
		String isAccepted = received.substring(beginIndex, endIndex);
		job.setAccepted(Boolean.valueOf(isAccepted));
		
		return job;
	}
}
