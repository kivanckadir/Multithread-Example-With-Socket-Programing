package server;

import java.net.Socket;

public interface FrameBehavior {
    
	void printResult(String consoleMessage);
	void makeAction(String message);
}