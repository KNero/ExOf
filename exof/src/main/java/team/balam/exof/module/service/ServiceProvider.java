package team.balam.exof.module.service;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.module.Module;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class ServiceProvider implements Module, Observer
{
	private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

	private Map<String, ServiceDirectory> serviceDirectory;
	private boolean isReloadServiceVariable;
	private volatile boolean isLoadingClass;

	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider()
	{
		
	}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}

    @SuppressWarnings("unchecked")
	private void register(ServiceDirectoryInfo info) throws Exception {
		Class<?> clazz = ExternalClassLoader.loadClass(info.getClassName());
		team.balam.exof.module.service.annotation.ServiceDirectory serviceDirAnn =
				clazz.getAnnotation(team.balam.exof.module.service.annotation.ServiceDirectory.class);

		if (serviceDirAnn != null) {
			Object host = clazz.newInstance();
			ServiceDirectory serviceDir = this.serviceDirectory.computeIfAbsent(info.getPath(), key -> new ServiceDirectory(host, key));

			Set<Method> services = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Service.class));
			for (Method m : services) {
				String serviceName = this.getServiceName(m);

				serviceDir.register(serviceName, m);

				if (logger.isInfoEnabled()) {
					logger.info("Service is loaded. path[{}] class[{}] name[{}]",
							info.getPath() + "/" + serviceName, info.getClassName(), serviceName);
				}
			}

			Set<Method> startup = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Startup.class));
			if (!startup.isEmpty()) {
				serviceDir.setStartup(startup.iterator().next());
			}

			Set<Method> shutdown = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Shutdown.class));
			if (!shutdown.isEmpty()) {
				serviceDir.setShutdown(shutdown.iterator().next());
			}
		} else {
			ServiceInfoDao.deleteServiceDirectory(info.getPath());
			throw new Exception("This class undefined ServiceDirectory annotation.");
		}
	}

	private String getServiceName(Method method) {
		String serviceName = method.getName();
		Service serviceAnnotation = method.getAnnotation(Service.class);

		if (!serviceAnnotation.value().isEmpty()) {
			serviceName = serviceAnnotation.value();
		}

		if (!serviceAnnotation.name().isEmpty()) {
			serviceName = serviceAnnotation.name();
		}

		return serviceName;
	}

	public static ServiceWrapper lookup(String path) throws ServiceNotFoundException {
		if (path == null || path.length() == 0) {
			throw new IllegalArgumentException("Path is null : " + path);
		}

		Map<String, ServiceDirectory> serviceDirectoryMap = self.serviceDirectory;

		self.waitLoadingService();

		int splitIdx = path.lastIndexOf("/");
		if (splitIdx == -1) {
			throw new ServiceNotFoundException(path);
		}

		String dirPath = path.substring(0, splitIdx);
		String serviceName = path.substring(splitIdx + 1);

		ServiceDirectory serviceDir = serviceDirectoryMap.get(dirPath);
		if (serviceDir == null) {
			throw new ServiceNotFoundException(path);
		}

		ServiceWrapper service = serviceDir.getService(serviceName);
		if (service == null) {
			throw new ServiceNotFoundException(path);
		}

		return service;
	}

	/**
	 * 현재 서비스 클래스가 로딩 중이라면 완료될 때 까지 기다린다.
	 * 만약 로딩이 1분 이상 지연된다면 에러로그를 출력한다.
	 */
	private void waitLoadingService() {
		if (self.isLoadingClass) {
			long start = System.currentTimeMillis();
			long sleepTime = 100;

			do {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// ignore exception
				}

				long elapsed = System.currentTimeMillis() - start;
				if (elapsed > 60000) {
					sleepTime = 1000;
					logger.error("Loading service class is lazy. {} ms", elapsed);
				}
			} while (self.isLoadingClass);
		}
	}

	public void loadServiceDirectory() {
		this.isLoadingClass = true;
		this.serviceDirectory = new HashMap<>();

		try {
			this.stop();

			List<ServiceDirectoryInfo> directoryInfoList = ServiceInfoDao.selectServiceDirectory();
			directoryInfoList.forEach(info -> {
				try {
					this.register(info);

					logger.warn("Service Directory is loaded.\n{}", info.toString());
				} catch(Exception e) {
					logger.error("Can not register the service. Class : {}", info.getClassName(), e);
				}
			});
		} catch (Exception e) {
			logger.error("Fail to stop ServiceDirectory.", e);
		} finally {
			this.isLoadingClass = false;
		}
	}

	@Override
	public void start() {
		Boolean isAutoReload = SystemSetting.getFramework(EnvKey.Framework.AUTORELOAD_SERVICE_VARIABLE);
		if (isAutoReload != null) {
			this.isReloadServiceVariable = isAutoReload;
		}

		this.loadServiceDirectory();
		
		this.serviceDirectory.values().forEach(ServiceDirectory::startup);
	}

	@Override
	public void stop() {
	    if (this.serviceDirectory != null) {
            this.serviceDirectory.values().forEach(ServiceDirectory::shutdown);
        }
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isReloadServiceVariable) {
			return;
		}

		String serviceDirPath = ((String[]) arg)[0];
		ServiceDirectory directory = this.serviceDirectory.get(serviceDirPath);
		if (directory != null) {
			directory.loadServiceVariable();
		}
	}
}
