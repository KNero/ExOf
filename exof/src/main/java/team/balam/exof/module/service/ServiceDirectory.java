package team.balam.exof.module.service;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Variable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

class ServiceDirectory {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceDirectory.class);
	
	private Object host;
	private String dirPath;
	
	private Method startup;
	private Method shutdown;
	private boolean isInternal;
	
	ServiceDirectory(Object host, String dirPath) {
		this.host = host;
		this.dirPath = dirPath;
	}

	void setStartup(Method startup)
	{
		this.startup = startup;
	}

	void setShutdown(Method shutdown)
	{
		this.shutdown = shutdown;
	}

	Object getHost() {
		return host;
	}

	void setInternal(boolean internal) {
		isInternal = internal;
	}

	boolean isInternal() {
		return isInternal;
	}

	void startup() {
		this.loadServiceVariable();

		if(this.startup != null) {
			try {
				this.startup.invoke(this.host);
			}
			catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error("Can not start the service. ServiceDirectory path : {}", this.dirPath, e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	void loadServiceVariable() {
		ServiceDirectoryInfo dirInfo = ServiceInfoDao.selectServiceDirectory(this.dirPath);

		this.loadPrimitiveVariable(dirInfo);

		Set<Field> fields = ReflectionUtils.getAllFields(host.getClass(), ReflectionUtils.withAnnotations(Service.class));
		for(Field field : fields) {
			field.setAccessible(true);
			Service serviceAnn = field.getAnnotation(Service.class);

			try {
				setService(field, !serviceAnn.value().isEmpty() ? serviceAnn.value() : serviceAnn.name());
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		fields = ReflectionUtils.getAllFields(host.getClass(), ReflectionUtils.withAnnotations(team.balam.exof.module.service.annotation.ServiceDirectory.class));
		for(Field field : fields) {
			team.balam.exof.module.service.annotation.ServiceDirectory directoryAnn =
					field.getAnnotation(team.balam.exof.module.service.annotation.ServiceDirectory.class);

			try {
				setServiceDirectory(field, !directoryAnn.value().isEmpty() ? directoryAnn.value() : directoryAnn.path());
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadPrimitiveVariable(ServiceDirectoryInfo dirInfo) {
		Set<Field> fields = ReflectionUtils.getAllFields(host.getClass(), ReflectionUtils.withAnnotation(Variable.class));
		for(Field field : fields) {
			field.setAccessible(true);

			if(field.getAnnotation(Variable.class) != null) {
				ServiceVariable serviceVariables = dirInfo.getVariable();
				if (serviceVariables == ServiceVariable.NULL_OBJECT) {
					return;
				}

				String value = serviceVariables.getString(field.getName());
				Class<?> fieldType = field.getType();

				try {
					if ("int".equals(fieldType.getName()) || fieldType.equals(Integer.class)) {
						field.set(host, Integer.valueOf(value));
					} else if ("long".equals(fieldType.getName()) || fieldType.equals(Long.class)) {
						field.set(host, Long.valueOf(value));
					} else if ("float".equals(fieldType.getName()) || fieldType.equals(Float.class)) {
						field.set(host, Float.valueOf(value));
					} else if ("double".equals(fieldType.getName()) || fieldType.equals(Double.class)) {
						field.set(host, Double.valueOf(value));
					} else if ("byte".equals(fieldType.getName()) || fieldType.equals(Byte.class)) {
						field.set(host, Byte.valueOf(value));
					} else if ("short".equals(fieldType.getName()) || fieldType.equals(Short.class)) {
						field.set(host, Short.valueOf(value));
					} else if (fieldType.equals(String.class)) {
						field.set(host, value);
					} else if (fieldType.equals(List.class)) {
						field.set(host, serviceVariables.get(field.getName()));
					} else {
						LOG.error("This type can not be set. ServiceDirectory:{}, Field type:{}",
								dirInfo.getPath(), fieldType);
					}
				} catch (IllegalAccessException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	private void setService(Field field, String servicePath) throws IllegalAccessException {
		Class<?> fieldType = field.getType();
		if (fieldType.equals(ServiceWrapper.class)) {
			if (!servicePath.isEmpty()) {
				try {
					ServiceWrapper serviceWrapper = ServiceProvider.lookup(new ServiceObject(servicePath));
					field.set(host, serviceWrapper);
				} catch (ServiceNotFoundException e) {
					LOG.error("Can not find service. {}", servicePath);
				}
			} else {
				LOG.error("Field's service annotation is must have full service path. ServiceDirectory:{}, Field:{}",
						this.dirPath, field.getName());
			}
		} else {
			LOG.error("This type can not be set. service path:{}, Field type:{}", servicePath, fieldType);
		}
	}

	private void setServiceDirectory(Field field, String serviceDirectoryPath)  throws IllegalAccessException {
		ServiceDirectory directory = ServiceProvider.getInstance().getServiceDirectory(serviceDirectoryPath);
		if (directory != null) {
			field.setAccessible(true);
			field.set(host, directory.getHost());
		} else {
			LOG.error("ServiceDirectory is not exists. path:{}", serviceDirectoryPath);
		}
	}
	
	void shutdown() {
		if(this.shutdown != null) {
			try {
				this.shutdown.invoke(this.host);
			}
			catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error("Can not stop the service. ServiceDirectory path : {}", this.dirPath, e);
			}
		}
	}
}
