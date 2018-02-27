package simple.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

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
				fixedThreadPool.execute(new Thread(new Connection(serverSocket.accept(), this)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
