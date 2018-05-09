package lists;


import java.awt.List;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import clienttype1.ClientType1;
import clienttype2.ClientType2;
import job.Job;

public class Lists {
	
	public static ArrayList<ClientType1> clientType1List = new ArrayList<>();
	public static ArrayList<ClientType2> clientType2List = new ArrayList<>();
	public static ArrayList <Job> jobs = new ArrayList<Job>();
	
	public static Map <String, String> activeUsers = new ConcurrentHashMap<String, String>();
	public static Map <String, String> usernamePassword = new ConcurrentHashMap<String, String>();
	
	public static Map <Integer, ClientType1> clientType1Map = new ConcurrentHashMap<Integer, ClientType1>();
	public static Map <Integer, ClientType2> clientType2Map = new ConcurrentHashMap<Integer, ClientType2>();
	public static Map <Integer, Job> jobMap = new ConcurrentHashMap<Integer, Job>();

	
}
