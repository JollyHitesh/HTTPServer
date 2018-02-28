package simple.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import simple.server.RequestParser.HttpMethod;
import simple.server.Response.StatusCode;

/**
 * 
 * @author Hitesh
 * 
 *         Thread class that reads the request, parses and writes the response
 *
 */

public class Connection implements Runnable {

	private Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			RequestParser parsedRequest = RequestParser.parseRequest(socket.getInputStream());
			if (parsedRequest != null) {
				System.out.println("Request for " + parsedRequest.getUrl() + " is being processed " + "by socket at "
						+ socket.getInetAddress() + ":" + socket.getPort());

				Response response;

				String method;
				if ((method = parsedRequest.getMethod()).equals(HttpMethod.GET) || method.equals(HttpMethod.HEAD)) {
					File f = new File("wwwroot" + parsedRequest.getUrl());
					response = new Response(StatusCode.OK).withFile(f);
					if (method.equals(HttpMethod.HEAD)) {
						response.removeBody();
					}
				} else {
					response = new Response(StatusCode.NOT_IMPLEMENTED);
				}
				respond(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void respond(Response response) throws IOException {
		String toSend = response.toString();
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.write(toSend);
		writer.flush();
	}

}
