package team.balam.exof;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.container.Framework;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.util.CollectionUtil;

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
		containerList.add(new Framework());
		containerList.add(SchedulerManager.getInstance());
		
		List<String> extraContainerList = SystemSetting.getInstance()
				.getListAndRemove(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.CONTAINER);
		
		CollectionUtil.doIterator(extraContainerList, _containerClass -> {
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
	
	public static void start()
	{
		CollectionUtil.doIterator(containerList, _container -> {
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
		
		_showSystemInfo();
	}
	
	private static void _showSystemInfo()
	{
		long maxHeap = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		int cpu = Runtime.getRuntime().availableProcessors();
		
		try
		{
			String ip = InetAddress.getLocalHost().getHostAddress();
			
			NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			StringBuilder sb = new StringBuilder();
			
			if(network != null)
			{
				byte[] mac = network.getHardwareAddress();
				for (int i = 0; i < mac.length; i++) 
				{
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
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
		CollectionUtil.doIterator(containerList, _container -> {
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
}
