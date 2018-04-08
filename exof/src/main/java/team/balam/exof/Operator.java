package team.balam.exof;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.container.Console;
import team.balam.exof.container.Framework;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 컨테이너의 구동을 담당한다.
 * @author kwonsm
 *
 */
public class Operator {
	private static Logger logger = LoggerFactory.getLogger(Operator.class);
	private static List<Container> containerList = new LinkedList<>();
	
	public static void init() {
		containerList.add(new Console());
		containerList.add(new Framework());
		containerList.add(SchedulerManager.getInstance());
		
		List<String> extraContainerList = SystemSetting.getFramework(EnvKey.Framework.CONTAINER);
		if(extraContainerList != null) {
			extraContainerList.forEach(containerClass -> {
				try {
					Container container = (Container) ExternalClassLoader.loadClass(containerClass).newInstance();
					containerList.add(container);
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					logger.error("Container[{}] can not create.", containerClass, e);
				}
			});
		}
	}
	
	public static void start() {
		containerList.forEach(container -> {
			try {
				container.start();
				logger.info("Container[{}] is started.", container.getName());
			} catch (InitializeFatalException e) {
				logger.error("Init fatal error occurred by starting container[{}].", container.getName(), e);
				System.exit(0);
			} catch (Exception e) {
				logger.error("Container[{}] can not start.", container.getName(), e);
			}
		});
		
		_showLogo();
		_showSystemInfo();
		
		SchedulerManager.getInstance().executeInitTimeAndStart();
	}
	
	private static void _showLogo() {
		logger.error("");
		logger.error("   =======             ===");
		logger.error("   |                  =   =     ==");
		logger.error("   =======   =   =   =     =   |");
		logger.error("   |           =      =   =  =====");
		logger.error("   =======   =   =     ===     |");
		logger.error("");
	}

	private static void _showSystemInfo() {
		long maxHeap = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		int cpu = Runtime.getRuntime().availableProcessors();

		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			StringBuilder sb = new StringBuilder();

			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			while (n.hasMoreElements()) {
				NetworkInterface e = n.nextElement();
				Enumeration<InetAddress> a = e.getInetAddresses();

				while (a.hasMoreElements()) {
					InetAddress addr = a.nextElement();
					if (!"127.0.0.1".equals(addr.getHostAddress()) && addr.getHostAddress().split("\\.").length == 4) {
						ip = addr.getHostAddress();
						NetworkInterface netif = NetworkInterface.getByInetAddress(addr);

						if (netif != null) {
							byte[] mac = netif.getHardwareAddress();
							if (mac != null) {
								if (sb.length() > 0) {
									sb.append(", ");
								}

								for (int i = 0; i < mac.length; i++) {
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
		} catch (Exception e) {
			logger.error("*************************************");
			logger.error("   The server is running normally.");
			logger.error("  ---------------------------------  ");
			logger.error("   CPU : " + cpu);
			logger.error("   Max Heap : " + maxHeap + " MB");
			logger.error("*************************************");
		}
	}

	public static void stop() {
		containerList.forEach(_container -> {
			try {
				_container.stop();
			} catch (Exception e) {
				logger.error("Container[{}] can not stop.", _container.getName(), e);
			}
		});

		logger.error("*************************************");
		logger.error("   The server has been shut down.");
		logger.error("*************************************");
	}
}
