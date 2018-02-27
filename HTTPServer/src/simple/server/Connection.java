package simple.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simple.server.RequestParser.HttpMethod;
import simple.server.Response.StatusCode;

public class Connection implements Runnable {

	private Socket socket;
	private Server server;
	private String method;
	private String url;
	private String protocol;
	private Map<String, String> headers = new HashMap<String, String>();
	private List<String> body = new ArrayList<String>();

	public Connection(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
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
