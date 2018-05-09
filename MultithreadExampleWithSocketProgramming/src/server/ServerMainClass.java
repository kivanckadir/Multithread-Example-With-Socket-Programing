package server;
import clienttype1.ClientType1;
import clienttype1.ClientType1Frame;
import clienttype2.ClientType2;
import clienttype2.ClientType2Frame;
import clienttype2.LoginFrame;

public class ServerMainClass {
	public static void main(String[] args) {
		
		ServerFrame serverFrame = new ServerFrame();
		Server server = new Server(serverFrame);
		serverFrame.setVisible(true);		
	}
}
