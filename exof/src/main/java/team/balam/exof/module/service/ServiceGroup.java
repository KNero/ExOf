package team.balam.exof.module.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.module.service.annotation.*;
import team.balam.exof.module.service.component.http.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
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

		ServiceInfoDao.insertLoadedService(service.getHost().getClass().toString(),
				serviceName, serviceName.equals(groupId) ? "" : groupId, method.toString());
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

	void merge(ServiceGroup other) {
		for (String otherGroup: other.group.keySet()) {
			for (String myGroup : group.keySet()) {
				if (myGroup.equals(otherGroup)) {
					log.error("Fail merge service group.",
							new ServiceAlreadyExistsException("class: " + other.serviceDirectoryHost.getClass() +
									", service group: " + otherGroup + ", service name: " + other.serviceName));
				}
			}

			group.put(otherGroup, other.group.get(otherGroup));
		}
	}

	@Override
	public String toString() {
		return "class: " + serviceDirectoryHost.getClass() + ", service group: " + group + ", service name: " + serviceName;
	}

	private interface ServiceMaker {
		void make(Annotation annotation, Method serviceMethod, boolean isInternal) throws Exception;
	}

	private class DefaultMaker implements ServiceMaker {
		@Override
		public void make(Annotation annotation, Method serviceMethod, boolean isInternal) throws Exception {
			if (Service.class.equals(annotation.annotationType())) {
				Service serviceAnn = (Service) annotation;
				String name = TreeServiceNodeMaker.standardizeServiceName(serviceAnn.name());
				if (name.isEmpty()) {
					name = TreeServiceNodeMaker.standardizeServiceName(serviceAnn.value());
				}

				if (serviceName.equals(name)) {
					String groupId = checkGroupId(serviceAnn.groupId());
					createService(groupId, serviceMethod, isInternal, null);
				}
			} else if (Services.class.equals(annotation.annotationType())) {
				for (Service service : ((Services) annotation).value()) {
					this.make(service, serviceMethod, isInternal);
				}
			}
		}
	}

	private class RestMaker implements ServiceMaker {
		@Override
		public void make(Annotation annotation, Method serviceMethod, boolean isInternal) throws Exception {
			if (RestService.class.equals(annotation.annotationType())) {
				RestService serviceAnn = (RestService) annotation;

				if (serviceName.equals(TreeServiceNodeMaker.standardizeServiceName(serviceAnn.name()))) {
					String groupId = checkGroupId(serviceAnn.method().name());
					createService(groupId, serviceMethod, isInternal, serviceWrapper -> {
						switch (serviceAnn.method()) {
							case POST:
								serviceWrapper.addInbound(HttpMethodFilter.POST);
								break;
							case GET:
								serviceWrapper.addInbound(HttpMethodFilter.GET);
								break;
							case PUT:
								serviceWrapper.addInbound(HttpMethodFilter.PUT);
								break;
							case DELETE:
								serviceWrapper.addInbound(HttpMethodFilter.DELETE);
								break;
							case PATCH:
								serviceWrapper.addInbound(HttpMethodFilter.PATCH);
								break;
							default:
								LoggerFactory.getLogger(RestMaker.class).error("not supported method. {}", serviceAnn.method());
						}

						serviceWrapper.addInbound(new QueryStringToMap());

						if (!Object.class.equals(serviceAnn.bodyToObject())) {
						    serviceWrapper.addInbound(new BodyToObject(serviceAnn.bodyToObject()));
                        }
					});
				}
			} else if (RestServices.class.equals(annotation.annotationType())) {
				for (RestService service : ((RestServices) annotation).value()) {
					this.make(service, serviceMethod, isInternal);
				}
			}
		}
	}
}
