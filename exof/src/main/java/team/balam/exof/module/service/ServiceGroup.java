package team.balam.exof.module.service;

import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServiceGroup {
	private String serviceName;
	private Object serviceDirectoryHost;

	private Map<String, ServiceWrapper> group = new HashMap<>();

	ServiceGroup(Object serviceDirectoryHost, String serviceName) {
		this.serviceDirectoryHost = serviceDirectoryHost;
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	void add(Method method, boolean isInternal) throws Exception {
		Service serviceAnn = method.getAnnotation(Service.class);
		String groupId = serviceAnn.groupId();
		if (groupId.isEmpty()) {
			groupId = serviceName;
		}

		if (group.containsKey(groupId)) {
			throw new ServiceLoadException(
					String.format("Service group id is duplicated. class[%s], group id: %s", serviceDirectoryHost.toString(), groupId));
		}

		ServiceWrapperImpl service = new ServiceWrapperImpl();
		service.setServiceName(serviceName);
		service.setHost(serviceDirectoryHost);
		service.setMethod(method);
		service.setInternal(isInternal);

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
}
