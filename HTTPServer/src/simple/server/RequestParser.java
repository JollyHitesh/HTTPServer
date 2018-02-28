package simple.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Hitesh Jolly
 * 
 *         Parses the received request from the input stream and breaks it down
 *         to its components i.e. method, url and protocol
 * 
 */

public class RequestParser {

	private String method;
	private String url;
	private String protocol;
	private Map<String, String> headers = new HashMap<String, String>();
	private List<String> body = new ArrayList<String>();

	public static RequestParser parseRequest(InputStream inputStream) {
		try {
			RequestParser requestParser = new RequestParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = reader.readLine();
			String[] request = line.split(" ", 3);
			requestParser.method = request[0];
			requestParser.url = request[1];
			requestParser.protocol = request[2];
			line = reader.readLine();
			while (line != null && !line.equals("")) {
				String[] header = line.split(": ", 2);
				if (header.length != 2)
					throw new IOException();
				else
					requestParser.headers.put(header[0], header[1]);
				line = reader.readLine();
			}
			while (reader.ready()) {
				line = reader.readLine();
				requestParser.body.add(line);
			}

			return requestParser;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		String result = method + " " + url + " " + protocol + "\n";
		for (String key : headers.keySet()) {
			result += key + ": " + headers.get(key) + "\n";
		}
		result += "\r\n";
		for (String line : body) {
			result += line + "\n";
		}
		return result;
	}

	public static class HttpMethod {
		public static final String GET = "GET";
		public static final String HEAD = "HEAD";
	}
}
