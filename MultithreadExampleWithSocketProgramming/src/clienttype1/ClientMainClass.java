package clienttype1;

import java.io.IOException;

public class ClientMainClass {
	
	public static void main(String[] args) {

		ClientType1Frame clientType1Frame = new ClientType1Frame();
		ClientType1 client = new ClientType1(clientType1Frame);
		clientType1Frame.setVisible(true);
	}
}
