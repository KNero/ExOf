package team.balam.exof.module.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.module.Module;
import team.balam.exof.module.service.annotation.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ServiceProvider implements Module, Observer
{
	private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

	private DirectoryTreeNode serviceTreeRoot;
	private boolean isReloadServiceVariable;
	private volatile boolean isLoadingClass;

	private static ServiceProvider self = new ServiceProvider();
	
	private ServiceProvider() {

	}

	ServiceDirectory getServiceDirectory(String path) {
		return serviceTreeRoot.findServiceDirectory(path);
	}
	
	public static ServiceProvider getInstance()
	{
		return self;
	}

	public static String getServiceName(Method method) {
		Service serviceAnnotation = method.getAnnotation(Service.class);
		String serviceName = "";

		if (!serviceAnnotation.value().isEmpty()) {
			serviceName = serviceAnnotation.value();
		}

		if (!serviceAnnotation.name().isEmpty()) {
			serviceName = serviceAnnotation.name();
		}

		return serviceName;
	}

	public static ServiceWrapper lookup(ServiceObject serviceObject) throws ServiceNotFoundException {
		String path = serviceObject.getServicePath();
		if (path == null || path.length() == 0) {
			throw new IllegalArgumentException("Path is null : " + path);
		}

		self.waitLoadingService();

		ServiceWrapper service = self.serviceTreeRoot.findService(serviceObject);
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
					logger.error(e.getMessage());
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
		serviceTreeRoot = DirectoryTreeNode.Builder.createRoot();

		try {
			this.stop();

			List<ServiceDirectoryInfo> directoryInfoList = ServiceInfoDao.selectServiceDirectory();
			directoryInfoList.forEach(info -> {
				try {
					DirectoryTreeNode.Builder.append(serviceTreeRoot, info.getPath(), info.getClassName());

					logger.warn("Service Directory is loaded.\n{}", info);
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
		serviceTreeRoot.findAllServiceDirectory().forEach(ServiceDirectory::startup);
	}

	@Override
	public void stop() {
		if (serviceTreeRoot != null) {
			serviceTreeRoot.findAllServiceDirectory().forEach(ServiceDirectory::shutdown);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!this.isReloadServiceVariable) {
			return;
		}

		String serviceDirPath = ((String[]) arg)[0];
		ServiceDirectory directory = serviceTreeRoot.findServiceDirectory(serviceDirPath);
		if (directory != null) {
			directory.loadServiceVariable();
		}
	}
}
