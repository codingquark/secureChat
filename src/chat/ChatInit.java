package chat;

import java.io.IOException;
import java.net.ServerSocket;

public class ChatInit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		boolean running = true;
		
		try {
			serverSocket = new ServerSocket(4444);
			System.out.print("Server at port 4444");
		} catch (IOException ioe) {
			System.out.println("Could not initialize the port 4444");
			System.exit(-1);
		}
		
		while(running) {
			try {
				new ChatThread(serverSocket.accept()).start();
			} catch (IOException ioe) {
				System.out.println("Could not accept the client.");
				ioe.printStackTrace();
				System.exit(-1);
			}
		}
		
		try {
			serverSocket.close();
		} catch (IOException ioe) {
			System.out.println("Could not close server port!");
			ioe.printStackTrace();
			System.exit(-1);
		}

	}

}
