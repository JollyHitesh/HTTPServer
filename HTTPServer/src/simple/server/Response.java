package simple.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Response {

	private static final String protocol = "HTTP/1.0";

	private String status;
	private NavigableMap<String, String> headers = new TreeMap<String, String>();
	private byte[] body = null;

	public Response(String status) {
		this.status = status;
		setDate(new Date());
	}

	public Response withFile(File f) {
		if (f.isFile()) {
			try {
				FileInputStream reader = new FileInputStream(f);
				int length = reader.available();
				body = new byte[length];
				reader.read(body);
				reader.close();

				setContentLength(length);
				if (f.getName().endsWith(".htm") || f.getName().endsWith(".html")) {
					setContentType(ContentType.HTML);
				} else {
					setContentType(ContentType.TEXT);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this;
		} else {
			return new Response(StatusCode.NOT_FOUND);
		}
	}

	public Response withHtmlBody(String msg) {
		setContentLength(msg.getBytes().length);
		setContentType(ContentType.HTML);
		body = msg.getBytes();
		return this;
	}

	public void setDate(Date date) {
		headers.put("Date", date.toString());
	}

	public void setContentLength(long value) {
		headers.put("Content-Length", String.valueOf(value));
	}

	public void setContentType(String value) {
		headers.put("Content-Type", value);
	}

	public void removeBody() {
		body = null;
	}

	@Override
	public String toString() {
		String result = protocol + " " + status + "\n";
		for (String key : headers.descendingKeySet()) {
			result += key + ": " + headers.get(key) + "\n";
		}
		result += "\r\n";
		if (body != null) {
			result += new String(body);
		}
		return result;
	}

	public static class StatusCode {
		public static final String OK = "200 OK";
		public static final String NOT_FOUND = "404 Not Found";
		public static final String NOT_IMPLEMENTED = "501 Not Implemented";
	}

	public static class ContentType {
		public static final String TEXT = "text/plain";
		public static final String HTML = "text/html";
	}

}
