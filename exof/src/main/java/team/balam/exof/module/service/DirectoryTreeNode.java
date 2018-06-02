package team.balam.exof.module.service;

import io.netty.util.internal.StringUtil;
import org.reflections.ReflectionUtils;
import team.balam.exof.Constant;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Outbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

class DirectoryTreeNode {
	private String name;
	private String pathVariableName;
	private boolean isWildcard;
	private HashMap<String, DirectoryTreeNode> childNode = new HashMap<>();

	private ServiceDirectory serviceDirectory;
	private ServiceWrapper service;

	private DirectoryTreeNode(String name) {
		this.name = name;
		char[] nameChar = name.toCharArray();
		isWildcard = nameChar[0] == '{' && nameChar[nameChar.length - 1] == '}';
		if (isWildcard) {
			pathVariableName = name.substring(1, name.length() - 1);
		}
	}

	private void appendChild(DirectoryTreeNode childNode) {
		this.childNode.put(childNode.name, childNode);
	}

	ServiceWrapper findService(ServiceObject serviceObject) {
		String[] path = validatePath(serviceObject.getServicePath());
		if (path.length == 0) {
			return service;
		}

		HashMap<String, String> pathVariable = new HashMap<>();
		serviceObject.setPathVariable(pathVariable);

		DirectoryTreeNode node = find(path, pathVariable);
		if (node != null) {
			return node.service;
		} else {
			return null;
		}
	}

	ServiceDirectory findServiceDirectory(String path) {
		String[] dirPath = validatePath(path);
		if (dirPath.length == 0) {
			return serviceDirectory;
		}

		DirectoryTreeNode node = find(dirPath);
		if (node != null) {
			return node.serviceDirectory;
		} else {
			return null;
		}
	}

	List<ServiceDirectory> findAllServiceDirectory() {
		ArrayList<ServiceDirectory> dirList = new ArrayList<>();

		getAllServiceDirectory(dirList);
		return dirList;
	}

	private void getAllServiceDirectory(ArrayList<ServiceDirectory> list) {
		if (this.serviceDirectory != null) {
			list.add(this.serviceDirectory);
		}

		for (DirectoryTreeNode child : childNode.values()) {
			child.getAllServiceDirectory(list);
		}
	}

	private DirectoryTreeNode find(String[] path, int depth, HashMap<String, String> pathVariable) {
		if (path.length == depth) {
			return null;
		}

		String currentPath = path[depth];
		if (StringUtil.isNullOrEmpty(currentPath)) {
			return this.find(path, ++depth, pathVariable);
		}

		if (isWildcard && pathVariable != null) {
			pathVariable.put(pathVariableName, currentPath);
			if (depth == path.length - 1) {
				return this;
			} else {
				currentPath = path[++depth];
			}
		}

		DirectoryTreeNode node = childNode.get(currentPath);
		if (node != null && path.length - 1 == depth) {
			return node;
		} else if (node != null) {
			return node.find(path, ++depth, pathVariable);
		} else {
			return findWildcard(path, depth, pathVariable);
		}
	}

	private DirectoryTreeNode findWildcard(String[] path, int depth, HashMap<String, String> pathVariable) {
		for (DirectoryTreeNode child : childNode.values()) {
			if (child.isWildcard) {
				DirectoryTreeNode node = child.find(path, depth, pathVariable);
				if (node != null) {
					return node;
				}
			}
		}

		return null;
	}

	private DirectoryTreeNode find(String[] path, HashMap<String, String> pathVariable) {
		return find(path, 0, pathVariable);
	}

	private DirectoryTreeNode find(String[] path) {
		return find(path, 0, null);
	}

	private static String[] validatePath(String servicePath) {
		String[] path = servicePath.split(Constant.SERVICE_SEPARATE);
		ArrayList<String> pathArray = new ArrayList<>();

		for (String p : path) {
			p = p.trim();

			if (!StringUtil.isNullOrEmpty(p)) {
				pathArray.add(p);
			}
		}

		if (!pathArray.isEmpty()) {
			return pathArray.toArray(new String[0]);
		} else {
			return new String[0];
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(name);
		if (serviceDirectory != null) {
			str.append(":").append(serviceDirectory);
		}
		return str.toString();
	}

	static class Builder {
		private Builder() {

		}

		/**
		 * Builder 를 통해서 root 만 외부로 노출된다.
		 * @return tree root node
		 */
		static DirectoryTreeNode createRoot() {
			return new DirectoryTreeNode("[root]");
		}

		static void append(DirectoryTreeNode root, String path, String className) throws Exception {
			Class<?> clazz = ExternalClassLoader.loadClass(className);
			ServiceDirectory newServiceDirectory = createServiceDirectory(path, clazz);
			List<ServiceWrapperImpl> serviceList = createServiceNode(newServiceDirectory, clazz);

			String[] dirPath = validatePath(path);
			int dirPathCount = 0;
			boolean isFindDirNode = false;

			if (dirPath.length == 0) {
				if (root.serviceDirectory != null) {
					throw new ServiceLoadException("Already exists directory. [" + path + "] " + className);
				} else {
					isFindDirNode = true;
					root.serviceDirectory = newServiceDirectory;
				}
			}

			for (ServiceWrapperImpl service : serviceList) {
				DirectoryTreeNode currentNode = root;
				String[] servicePath = validatePath(path + Constant.SERVICE_SEPARATE + service.getServiceName());
				ArrayList<String> pathArray = new ArrayList<>();

				for (int i = 0; i < servicePath.length; ++i) {
					String pathPart = servicePath[i];
					pathArray.add(pathPart);

					DirectoryTreeNode node = root.find(Arrays.copyOf(servicePath, pathArray.size()), 0, null);

					if (node == null) {
						DirectoryTreeNode newNode = new DirectoryTreeNode(pathPart);
						currentNode.appendChild(newNode);
						currentNode = newNode;
					} else {
						currentNode = node;
					}

					if (!isFindDirNode && i < dirPath.length && dirPath[i].equals(servicePath[i])) {
						// service path 를 찾으면서 service directory path 를 찾아서 ServiceDirectory 를 생성해 준다.
						++dirPathCount;

						if (dirPathCount == dirPath.length) {
							if (currentNode.serviceDirectory == null) {
								isFindDirNode = true;
								currentNode.serviceDirectory = newServiceDirectory;
							} else {
								throw new ServiceLoadException("Already exists directory. [" + path + "] " + className);
							}
						}
					}
				}

				if (currentNode.service == null) {
					currentNode.service = service;
				} else {
					throw new ServiceAlreadyExistsException(service.getHost() + ". Method:" + service.getMethodName());
				}
			}
		}

		@SuppressWarnings("unchecked")
		private static ServiceDirectory createServiceDirectory(String path, Class<?> clazz) throws Exception {
			team.balam.exof.module.service.annotation.ServiceDirectory serviceDirAnn =
					clazz.getAnnotation(team.balam.exof.module.service.annotation.ServiceDirectory.class);
			if (serviceDirAnn != null) {
				ServiceDirectory newServiceDirectory = new ServiceDirectory(clazz.newInstance(), path);
				newServiceDirectory.setInternal(serviceDirAnn.internal());

				Set<Method> startup = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Startup.class));
				if (!startup.isEmpty()) {
					newServiceDirectory.setStartup(startup.iterator().next());
				}

				Set<Method> shutdown = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Shutdown.class));
				if (!shutdown.isEmpty()) {
					newServiceDirectory.setShutdown(shutdown.iterator().next());
				}

				return newServiceDirectory;
			} else {
				ServiceInfoDao.deleteServiceDirectory(path);
				throw new ServiceLoadException("This class undefined ServiceDirectory annotation.");
			}
		}

		@SuppressWarnings("unchecked")
		private static List<ServiceWrapperImpl> createServiceNode(ServiceDirectory serviceDirectory, Class<?> clazz) throws Exception {
			ArrayList<ServiceWrapperImpl> serviceList = new ArrayList<>();

			Set<Method> services = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(Service.class));
			for (Method m : services) {
				String serviceName = ServiceProvider.getServiceName(m);

				ServiceWrapperImpl service = new ServiceWrapperImpl();
				service.setServiceName(serviceName);
				service.setHost(serviceDirectory.getHost());
				service.setMethod(m);
				service.setInternal(serviceDirectory.isInternal());

				checkInboundAnnotation(m, service);
				checkOutboundAnnotation(m, service);

				serviceList.add(service);
			}

			return serviceList;
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


	}
}
