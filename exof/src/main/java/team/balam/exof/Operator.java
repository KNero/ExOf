package team.balam.exof;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.container.Console;
import team.balam.exof.container.Framework;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;

/**
 * 컨테이너의 구동을 담당한다.
 * @author kwonsm
 *
 */
public class Operator 
{
	private static Logger logger = LoggerFactory.getLogger(Operator.class);
	private static List<Container> containerList = new LinkedList<Container>();
	
	public static void init()
	{
		containerList.add(new Console());
		containerList.add(new Framework());
		containerList.add(SchedulerManager.getInstance());
		
		List<String> extraContainerList = SystemSetting.getInstance().getList(EnvKey.FileName.FRAMEWORK, EnvKey.Framework.CONTAINER);
		if(extraContainerList != null)
		{
			extraContainerList.forEach(_containerClass -> {
				try 
				{
					Container container = (Container)Class.forName(_containerClass).newInstance();
					containerList.add(container);
				} 
				catch (Exception e) 
				{
					if(e instanceof InitializeFatalException)
					{
						logger.error("Init fatal error occurred by init container[{}].", _containerClass, e);
						
						System.exit(0);
					}
					else
					{
						logger.error("Container[{}] can not create.", _containerClass, e);
					}
				}
			});
		}
	}
	
	public static void start()
	{
		containerList.forEach(_container -> {
			try
			{
				_container.start();
			}
			catch(Exception e) 
			{ 
				if(e instanceof InitializeFatalException)
				{
					logger.error("Init fatal error occurred by starting container[{}].", _container.getName(), e);
					
					System.exit(0);
				}
				else
				{
					logger.error("Container[{}] can not start.", _container.getName(), e);
				}
			}
		});
		
		logger.error("");
		logger.error("   =======             ===");
		logger.error("   |                  =   =     ==");
		logger.error("   =======   =   =   =     =   |");
		logger.error("   |           =      =   =  =====");
		logger.error("   =======   =   =     ===     |");
		logger.error("");
		
		_showSystemInfo();
	}
	
	private static void _showSystemInfo()
	{
		long maxHeap = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		int cpu = Runtime.getRuntime().availableProcessors();
		
		try
		{
			String ip = InetAddress.getLocalHost().getHostAddress();
			StringBuilder sb = new StringBuilder();
			
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		    while(n.hasMoreElements())
		    {
		        NetworkInterface e = n.nextElement();
		        Enumeration<InetAddress> a = e.getInetAddresses();
		        
		        while(a.hasMoreElements())
		        {
		            InetAddress addr = a.nextElement();
		            if(! "127.0.0.1".equals(addr.getHostAddress()) && addr.getHostAddress().split("\\.").length == 4)
		            {
		            	ip = addr.getHostAddress();
		            	NetworkInterface netif = NetworkInterface.getByInetAddress(addr);

						if(netif != null)
						{
							byte[] mac = netif.getHardwareAddress();
							if(mac != null)
							{
								for(int i = 0; i < mac.length; i++)
								{
									sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
								}
								
								break;
							}
						}
		            }
		        }
		    }
			
			logger.error("*************************************");
			logger.error("   The server is running normally.");
			logger.error("  ---------------------------------  ");
			logger.error("   CPU : " + cpu);
			logger.error("   Max Heap : " + maxHeap + " MB");
			logger.error("   IP : " + ip);
			logger.error("   MAC address : " + sb.toString());
			logger.error("*************************************");
		}
		catch(Exception e)
		{
			logger.error("*************************************");
			logger.error("   The server is running normally.");
			logger.error("  ---------------------------------  ");
			logger.error("   CPU : " + cpu);
			logger.error("   Max Heap : " + maxHeap + " MB");
			logger.error("*************************************");
		}
	}
	
	public static void stop()
	{
		containerList.forEach(_container -> {
			try
			{
				_container.stop();
			}
			catch(Exception e) { logger.error("Container[{}] can not stop.", _container.getName(), e); }
		});
		
		logger.error("*************************************");
		logger.error("   The server has been shut down.");
		logger.error("*************************************");
	}
	
	public static void main(String[] args) throws Exception
	{
	    System.out.println("Your Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
	    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
	    while(n.hasMoreElements())
	    {
	        NetworkInterface e = n.nextElement();

	        Enumeration<InetAddress> a = e.getInetAddresses();
	        while(a.hasMoreElements())
	        {
	            InetAddress addr = a.nextElement();
	            System.out.println("  " + addr.getHostAddress());
	            
				// 네트워크 인터페이스 취득
				NetworkInterface netif = NetworkInterface.getByInetAddress(addr);
				// 네트워크 인터페이스가 NULL이 아니면
				if(netif != null)
				{
					// 네트워크 인터페이스 표시명 출력
					System.out.print(netif.getDisplayName() + " : ");

					// 맥어드레스 취득
					byte[] mac = netif.getHardwareAddress();
					if(mac != null)
					{
						for(byte b : mac)
						{
							System.out.printf("[%02X]", b);
						}
					}
				}
	        }
	    }
	} 
}
