package team.balam.exof.container.console.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import team.balam.exof.container.console.Command;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.ListenerLoader;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.util.StreamUtil;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Client
{
	private static int consolePort;
	
	public static void main(String[] _arge) throws Exception
	{
		init();
		
		new Viewer().start();
	}
	
	public static void init() throws Exception
	{
		String envPath = System.getProperty(EnvKey.ENV_PATH, "./env");
		String filePath = envPath + "/listener.xml";
		InputStream input = null;

		try {
			input = new FileInputStream(new File(filePath));
			InputSource is = new InputSource(input);

			XPath xpath = XPathFactory.newInstance().newXPath();
			consolePort = Integer.parseInt(xpath.evaluate("//port[@console]/@number", new InputSource(input)));
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	public static void send(Command _command, Consumer<Object> _successCallback, Consumer<Object> _failCallback) throws IOException
	{
		Socket socket = null;
		String jsonStr = null;
		
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(consolePort), 3000);
			socket.setSoTimeout(5000);

			OutputStream out = socket.getOutputStream();
			out.write(_command.toJson().getBytes());
			out.write('\0');

			InputStream in = socket.getInputStream();
			byte[] res = StreamUtil.read(in, '\0');
			jsonStr = new String(res);

			Object result;
			ObjectMapper objectMapper = new ObjectMapper();

			if (jsonStr.startsWith("{")) {
				TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>() {};
				result = objectMapper.readValue(jsonStr, mapType);
			} else if (jsonStr.startsWith("[")) {
				TypeReference<List<Object>> listType = new TypeReference<List<Object>>() {};
				result = objectMapper.readValue(jsonStr, listType);
			} else {
				result = jsonStr;
			}

			if (_successCallback != null && _isExistData(result)) {
				_successCallback.accept(result);
			} else if (_failCallback != null) {
				_failCallback.accept(result);
			}
		} catch (IOException e) {
			System.err.println("Response : " + jsonStr);
			throw e;
		}
		finally
		{
			if(socket != null)
			{
				try 
				{
					socket.close();
				}
				catch(IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean _isExistData(Object _result) {
		if (_result instanceof Map) {
			String resultValue = (String) ((Map<String, Object>) _result).get(Command.Key.RESULT);
			if (resultValue != null) {
				System.out.println(resultValue);
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
}
