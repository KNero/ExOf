package team.balam.exof.module.service;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Variable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ServiceDirectory {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceDirectory.class);
	
	private Object host;
	private String dirPath;
	
	private Method startup;
	private Method shutdown;
	
	private Map<String, ServiceWrapper> serviceMap = new ConcurrentHashMap<>();
	
	ServiceDirectory(Object host, String dirPath) {
		this.host = host;
		this.dirPath = dirPath;
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

		Set<Field> fields = ReflectionUtils.getAllFields(host.getClass(), ReflectionUtils.withAnnotation(Service.class));
		for(Field field : fields) {
			field.setAccessible(true);

			Service serviceAnn = field.getAnnotation(Service.class);
			if(serviceAnn != null) {
				ServiceVariable serviceVariables = dirInfo.getVariable();
				if (serviceVariables == ServiceVariable.NULL_OBJECT) {
					return;
				}

				try {
					Class<?> fieldType = field.getType();
					if (fieldType.equals(ServiceWrapper.class)) {
						this.setService(field, serviceAnn.value());
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
		if (!servicePath.isEmpty()) {
			try {
				ServiceWrapper serviceWrapper = ServiceProvider.lookup(servicePath);
				field.set(host, serviceWrapper);
			} catch (ServiceNotFoundException e) {
				LOG.error("Can not find service. {}", servicePath);
			}
		} else {
			LOG.error("Field's service annotation is must have full service path. ServiceDirectory:{}, Field:{}",
					this.dirPath, field.getName());
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
	
	void setStartup(Method startup)
	{
		this.startup = startup;
	}
	
	void setShutdown(Method shutdown)
	{
		this.shutdown = shutdown;
	}

	void register(String serviceName,Method method) throws Exception {
		if (this.serviceMap.containsKey(serviceName)) {
			throw new ServiceAlreadyExistsException(this.dirPath + "/" + serviceName);
		}
		Service annotation = method.getAnnotation(Service.class);

		ServiceWrapperImpl service = new ServiceWrapperImpl();
		service.setHost(this.host);
		service.setMethod(method);
		service.setInternal(annotation.internal());

		this.checkInboundAnnotation(method, service);
		this.checkOutboundAnnotation(method, service);

		this.serviceMap.put(serviceName, service);
	}

	private void checkInboundAnnotation(Method method, ServiceWrapperImpl service) throws Exception {
		Inbound inboundAnn = method.getAnnotation(Inbound.class);
		if (inboundAnn != null) {
			for (Class<? extends team.balam.exof.module.service.component.Inbound> clazz : inboundAnn.value()) {
				service.addInbound(clazz.newInstance());
			}
		}
	}

	private void checkOutboundAnnotation(Method method, ServiceWrapperImpl service) throws Exception {
		Outbound outboundAnn = method.getAnnotation(Outbound.class);
		if (outboundAnn != null) {
			for (Class<? extends team.balam.exof.module.service.component.Outbound<?, ?>> clazz : outboundAnn.value())
			service.addOutbound(clazz.newInstance());
		}
	}
	
	ServiceWrapper getService(String serviceName) {
		return this.serviceMap.get(serviceName);
	}
}
