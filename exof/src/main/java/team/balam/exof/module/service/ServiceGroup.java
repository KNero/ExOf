package team.balam.exof.module.service;

import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.component.http.HttpMethod;
import team.balam.exof.module.service.component.http.HttpMethodFilter;
import team.balam.exof.module.service.component.http.JsonToObject;
import team.balam.exof.module.service.component.http.QueryStringToMap;
import team.balam.exof.module.service.component.http.RestService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ServiceGroup {
	private String serviceName;
	private Object serviceDirectoryHost;
	private List<ServiceMaker> serviceMakerList = new ArrayList<>();

	private Map<String, ServiceWrapper> group = new HashMap<>();

	ServiceGroup(Object serviceDirectoryHost, String serviceName) {
		this.serviceDirectoryHost = serviceDirectoryHost;
		this.serviceName = serviceName;

		serviceMakerList.add(new DefaultMaker());
		serviceMakerList.add(new RestMaker());
	}

	String getServiceName() {
		return serviceName;
	}

	void add(Method method, boolean isInternal) throws Exception {
		Annotation[] annotationList = method.getAnnotations();
		for (Annotation ann : annotationList) {
			for (ServiceMaker maker : serviceMakerList) {
				maker.make(ann, method, isInternal);
			}
		}
	}

	private String checkGroupId(String groupId) throws ServiceLoadException {
		if (groupId.isEmpty()) {
			groupId = serviceName;
		}

		if (group.containsKey(groupId)) {
			throw new ServiceLoadException(
					String.format("Service group id is duplicated. class[%s], group id: %s", serviceDirectoryHost.toString(), groupId));
		}

		return groupId;
	}

	private void createService(String groupId, Method method, boolean isInternal, Consumer<ServiceWrapperImpl> preInOutBound) throws Exception {
		ServiceWrapperImpl service = new ServiceWrapperImpl();
		service.setServiceName(serviceName);
		service.setHost(serviceDirectoryHost);
		service.setMethod(method);
		service.setInternal(isInternal);

		if (preInOutBound != null) {
			preInOutBound.accept(service);
		}

		checkInboundAnnotation(method, service);
		checkOutboundAnnotation(method, service);

		group.put(groupId, service);
	}


	private static void checkInboundAnnotation(Method method, ServiceWrapperImpl service) throws Exception {
		Inbound inboundAnn = method.getAnnotation(Inbound.class);
		if (inboundAnn != null) {
			for (Class<? extends team.balam.exof.module.service.component.Inbound> clazz : inboundAnn.value()) {
				service.addInbound(clazz.newInstance());
			}
		}
	}

	private static void checkOutboundAnnotation(Method method, ServiceWrapperImpl service) throws Exception {
		Outbound outboundAnn = method.getAnnotation(Outbound.class);
		if (outboundAnn != null) {
			for (Class<? extends team.balam.exof.module.service.component.Outbound<?, ?>> clazz : outboundAnn.value())
				service.addOutbound(clazz.newInstance());
		}
	}

	ServiceWrapper getService(String key) {
		if (key == null) {
			return group.get(serviceName);
		} else {
			return group.getOrDefault(key, group.get(serviceName));
		}
	}

	@Override
	public String toString() {
		return "class: " + serviceDirectoryHost.getClass() + ", service name: " + serviceName;
	}

	private interface ServiceMaker {
		void make(Annotation annotation, Method serviceMethod, boolean isInternal) throws Exception;
	}

	private class DefaultMaker implements ServiceMaker {
		@Override
		public void make(Annotation annotation, Method serviceMethod, boolean isInternal) throws Exception {
			if (annotation.annotationType() == Service.class) {
				Service serviceAnn = (Service) annotation;
				String groupId = checkGroupId(serviceAnn.groupId());

				if (group.containsKey(groupId)) {
					throw new ServiceLoadException("groupId is duplicated. method: " + serviceMethod);
				}

				createService(groupId, serviceMethod, isInternal, null);
			}
		}
	}

	private class RestMaker implements ServiceMaker {
		@Override
		public void make(Annotation annotation, Method serviceMethod, boolean isInternal) throws Exception {
			if (annotation.annotationType() == RestService.class) {
				RestService serviceAnn = (RestService) annotation;
				String groupId = checkGroupId(serviceAnn.method().name());

				if (group.containsKey(groupId)) {
					throw new ServiceLoadException("groupId is duplicated. method: " + serviceMethod);
				}

				createService(groupId, serviceMethod, isInternal, serviceWrapper -> {
					if (serviceAnn.method() == HttpMethod.POST) {
						serviceWrapper.addInbound(HttpMethodFilter.POST);
					} else if (serviceAnn.method() == HttpMethod.GET) {
						serviceWrapper.addInbound(HttpMethodFilter.GET);
					} else if (serviceAnn.method() == HttpMethod.PUT) {
						serviceWrapper.addInbound(HttpMethodFilter.PUT);
					} else if (serviceAnn.method() == HttpMethod.DELETE) {
						serviceWrapper.addInbound(HttpMethodFilter.DELETE);
					} else if (serviceAnn.method() == HttpMethod.PATCH) {
						serviceWrapper.addInbound(HttpMethodFilter.PATCH);
					}
				});
			}
		}
	}
}
