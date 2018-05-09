package clienttype2;

import java.io.IOException;

public class ClientMainClass {
	
	public static void main(String[] args) {

		LoginFrame loginFrame = new LoginFrame();
		ClientType2 client = new ClientType2(loginFrame);
		loginFrame.setVisible(true);
	}
}
