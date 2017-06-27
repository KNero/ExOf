package team.balam.exof.container.console.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import team.balam.exof.Constant;
import team.balam.exof.container.console.Command;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.ListenerLoader;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.util.StreamUtil;

public class Client
{
	private static int consolePort;
	
	public static void main(String[] _arge) throws Exception
	{
		Client.init();
		
		new Viewer().start();
	}
	
	public static void init() throws Exception
	{
		String envPath = System.getProperty(EnvKey.HOME, "./env");
		ListenerLoader loader = new ListenerLoader();
		loader.load(envPath);
		
		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.FileName.LISTENER, EnvKey.Listener.PORT);
		for(PortInfo port : portList)
		{
			if(Constant.YES.equals(port.getAttribute(EnvKey.Listener.CONSOLE)))
			{
				consolePort = port.getAttributeToInt(EnvKey.Listener.NUMBER, 0);
				break;
			}
		}
	}
	
	public static void send(Command _command, Consumer<Object> _successCallback, Consumer<Object> _failCallback) throws IOException
	{
		Socket socket = null;
		
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress(consolePort), 3000);
			socket.setSoTimeout(5000);
			
			OutputStream out = socket.getOutputStream();
			out.write(_command.toJson().getBytes());
			out.write('\0');
			
			InputStream in = socket.getInputStream();
			byte[] res = StreamUtil.read(in, '\0');
			String jsonStr = new String(res);

			Object result = null;
			ObjectMapper objectMapper = new ObjectMapper();

			if (jsonStr.startsWith("{")) {
				TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
				result = objectMapper.readValue(jsonStr, mapType);
			} else {
				TypeReference<List<Object>> mapType = new TypeReference<List<Object>>(){};
				result = objectMapper.readValue(jsonStr, mapType);
			}

			if(_successCallback != null && _isExistData(result))
			{
				_successCallback.accept(result);
			}
			else if(_failCallback != null)
			{
				_failCallback.accept(result);
			}
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
