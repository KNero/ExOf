package team.balam.exof.util;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SimpleHttpInvoker {
	private static final int SUCCESS_RES = 200;

	private URL url;
	private int connectTimeout;
	private int readTimeout;
	private String charset;

	public SimpleHttpInvoker(String _url) throws MalformedURLException {
		this(new URL(_url));
	}

	public SimpleHttpInvoker(URL _url) throws MalformedURLException {
		String protocol = _url.getProtocol();
		if (!protocol.equals("http") && !protocol.equals("https")) {
			throw new MalformedURLException("Only invoke http and https.");
		}

		this.url = _url;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public URL getUrl() {
		return url;
	}

	private HttpURLConnection _createConnection(String _method) throws IOException {
		HttpURLConnection con = (HttpURLConnection) this.url.openConnection();
		con.setConnectTimeout(this.connectTimeout);
		con.setReadTimeout(this.readTimeout);
		con.setRequestMethod(_method);
		con.setDoInput(true);
		con.setDoOutput(true);

		return con;
	}

	public String invokeGet() throws Exception {
		return this.invoke("GET", null);
	}

	public String invoketPost(byte[] _body) throws Exception {
		return this.invoke("POST", _body);
	}
	
	private String invoke(String _method, byte[] _body) throws Exception {
		HttpURLConnection con = null;
		BufferedReader resIn = null;

		try {
			con = this._createConnection(_method);
			
			if (_body != null) {
				OutputStream bodyOut = null;
				
				try {
					bodyOut = con.getOutputStream();
					bodyOut.write(_body);
					bodyOut.flush();
				} finally {
					if (bodyOut != null) {
						bodyOut.close();
					}
				}
			}

			if (con.getResponseCode() == SUCCESS_RES) {
				InputStreamReader inputReader = new InputStreamReader(con.getInputStream(), this.charset); 
				resIn = new BufferedReader(inputReader);
				int read = 0;
				char[] buf = new char[1024];
				CharArrayWriter out = new CharArrayWriter();
				
				while ((read = resIn.read(buf)) != -1) {
					out.write(buf, 0, read);
				}

				if (out.size() > 0) {
					return out.toString();
				} else {
					return "";
				}
			} else {
				throw new Exception("Fail to invoke " + _method + ". Response code [" + con.getResponseCode() + "] : "
						+ con.getResponseMessage());
			}
		} finally {
			if (resIn != null) {
				resIn.close();
			}

			if (con != null) {
				con.disconnect();
			}
		}
	}
}
