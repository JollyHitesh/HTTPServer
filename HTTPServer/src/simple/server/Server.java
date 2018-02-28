package simple.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

/**
 * 
 * @author Hitesh Jolly
 * 
 *         Main class, defines the port number and the maximum thread count
 *         receives the request and creates a server socket using the
 *         {@ServerSocketFactory} passes on the received request to worker
 *         threads to process
 *
 */
public class Server implements Runnable {

	private static final int port = 8080;
	private static final int threadsLimit = 50;

	public static void main(String args[]) {
		new Thread(new Server()).start();
	}

	@Override
	public void run() {

		try {
			ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadsLimit);
			while (!Thread.interrupted()) {
				fixedThreadPool.execute(new Thread(new Connection(serverSocket.accept())));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
