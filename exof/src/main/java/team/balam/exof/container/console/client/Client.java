package team.balam.exof.container.console.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT);
		for(PortInfo port : portList)
		{
			if(Constant.YES.equals(port.getAttribute(EnvKey.Listener.CONSOLE)))
			{
				consolePort = port.getAttributeToInt(EnvKey.Listener.PORT, 0);
				break;
			}
		}
	}
	
	public static void send(Command _command, java.util.function.Consumer<Map<String, Object>> _callback) throws IOException
	{
		Socket socket = null;
		
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress(consolePort), 3000);
			socket.setSoTimeout(5000);
			
			OutputStream out = socket.getOutputStream();
			out. write(_command.toJson().getBytes());
			
			InputStream in = socket.getInputStream();
			byte[] res = StreamUtil.read(in, '\0');
			
			ObjectMapper objectMapper = new ObjectMapper();
			TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
			
			_callback.accept(objectMapper.readValue(new String(res), mapType));
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
}
