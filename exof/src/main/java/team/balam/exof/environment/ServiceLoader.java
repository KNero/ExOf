package team.balam.exof.environment;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import team.balam.exof.Constant;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.vo.NodeImpl;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.Set;

public class ServiceLoader implements Loader {
	private int scheduleCount;

	@Override
	public void load(String envPath) throws LoadEnvException
	{
		String filePath = envPath + "/service.xml";
		if (new File(filePath).exists()) {
			this.loadServiceAndScheduler(filePath);
		} else {
			throw new LoadEnvException("Service file is not exists. " + filePath);
		}
	}

	private void loadServiceAndScheduler(String filePath) throws LoadEnvException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File(filePath));

			Node servicesNode = doc.getFirstChild();
			if (this.equalsNodeName(servicesNode, EnvKey.Service.SERVICES)) {
				Node serviceNode = servicesNode.getFirstChild();

				while (serviceNode != null) {
					if (this.equalsNodeName(serviceNode, EnvKey.Service.SERVICE_DIRECTORY)) {
						this.insertServiceDirectoryInfo(serviceNode);
					} else if (this.equalsNodeName(serviceNode, EnvKey.Service.SCHEDULER)) {
						String[] pathArr = filePath.split("/");
						String fileName = pathArr[pathArr.length - 1];

						this.insertSchedulerInfo(fileName, serviceNode);
					} else if (this.equalsNodeName(serviceNode, EnvKey.Service.RESOURCE)) {
						String serviceFile = serviceNode.getAttributes().getNamedItem(EnvKey.Service.FILE).getNodeValue();
						if (!new File(serviceFile).exists()) {
							throw new FileNotFoundException("input file path : [" + serviceFile + "]");
						}

						this.loadServiceAndScheduler(serviceFile);
					} else if (this.equalsNodeName(serviceNode, EnvKey.Service.SERVICE_PACKAGE)) {
						this.scanServicePackage(serviceNode);
					}

					serviceNode = serviceNode.getNextSibling();
				}
			}
		} catch (Exception e) {
			throw new LoadEnvException(filePath, e);
		}
	}
	
	private boolean equalsNodeName(Node node, String name)
	{
		return node != null && name.equals(node.getNodeName());
	}
	
	private void insertServiceDirectoryInfo(Node node) throws LoadEnvException {
		NamedNodeMap attr = node.getAttributes();
		String path = attr.getNamedItem(EnvKey.Service.PATH).getNodeValue();
		String className = attr.getNamedItem(EnvKey.Service.CLASS).getNodeValue();

		if (!className.isEmpty()) {
			ServiceInfoDao.insertServiceDirectory(path, className);
		}

		Node variableNode = node.getFirstChild();
		while(variableNode != null) {
			if(this.equalsNodeName(variableNode, EnvKey.Service.VARIABLE)) {
				this.loadServiceVariable(path, variableNode);
			}
			
			variableNode = variableNode.getNextSibling();
		}
	}

	private void loadServiceVariable(String directoryPath, Node variableNode) throws LoadEnvException {
		NamedNodeMap variableAttr = variableNode.getAttributes();
		String name = variableAttr.getNamedItem(EnvKey.Service.NAME).getNodeValue();
		String value = variableAttr.getNamedItem(EnvKey.Service.VALUE).getNodeValue();

		ServiceInfoDao.insertServiceVariable(directoryPath, name, value);
	}
	
	/**
	 * xml의 정보를 통해 스케쥴러를 생성한다.
	 * @param fileName 스케쥴러 자동 아이디 생성을 위해서 prefix 로 사용된다.
	 * @param node 스케쥴러 정보를 갖고있는 xml node
	 */
	private void insertSchedulerInfo(String fileName, Node node) throws LoadEnvException {
		NamedNodeMap attr = node.getAttributes();

		String servicePath = attr.getNamedItem(EnvKey.Service.SERVICE_PATH).getNodeValue();
		String cron = attr.getNamedItem(EnvKey.Service.CRON).getNodeValue();
		String isDuplicateExecution = attr.getNamedItem(EnvKey.Service.DUPLICATE_EXECUTION).getNodeValue();
		String isUse = attr.getNamedItem(EnvKey.Service.USE).getNodeValue();
		String isInitExecution = attr.getNamedItem(EnvKey.Service.INIT_EXECUTION).getNodeValue();

		String id;
		Node idAttr = attr.getNamedItem(EnvKey.Service.ID);
		if (idAttr != null && idAttr.getNodeValue().trim().length() > 0) {
			id = idAttr.getNodeValue();
		} else {
			id = fileName + "-scheduler-" + this.scheduleCount++;
		}

		try {
			if (ServiceInfoDao.selectScheduler(id).isNull()) {
				ServiceInfoDao.insertSchedule(id, servicePath, cron, isDuplicateExecution, isUse, isInitExecution);
			}
		} catch (Exception e) {
			throw new LoadEnvException("service.xml", e);
		}
	}

	private void scanServicePackage(Node servicePackageNode) throws LoadEnvException {
		Node attribute = servicePackageNode.getAttributes().getNamedItem(EnvKey.Service.PACKAGE);
		if (attribute != null) {
			String packageName = attribute.getNodeValue();
			Reflections reflections = new Reflections(new ConfigurationBuilder()
					.addClassLoader(ExternalClassLoader.getClassLoader())
					.setUrls(ClasspathHelper.forClassLoader(ExternalClassLoader.getClassLoader()))
					.setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
					.filterInputsBy(new FilterBuilder().includePackage(packageName)));

			Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(ServiceDirectory.class);
			for (Class<?> serviceDirectory : classSet) {
				ServiceDirectory annotation = serviceDirectory.getAnnotation(ServiceDirectory.class);
				String dirPath = annotation.value().isEmpty() ? annotation.path() : annotation.value();

				if (!dirPath.isEmpty()) {
					ServiceInfoDao.insertServiceDirectory(dirPath, serviceDirectory.getName());

					Method[] methods = serviceDirectory.getMethods();
					for (Method method : methods) {
						this.insertScheduleFromAutoScan(dirPath, method);
					}
				} else {
					Logger logger = LoggerFactory.getLogger(ServiceLoader.class);
					logger.info("Skip loading service directory because path is empty. [{}]", serviceDirectory.getName());
				}
			}
		}
	}

	private void insertScheduleFromAutoScan(String serviceDirectoryPath, Method method) throws LoadEnvException {
		Service annotation = method.getAnnotation(Service.class);
		if (annotation != null && !annotation.schedule().isEmpty()) {
			String serviceName = method.getName();
			if (!annotation.name().isEmpty()) {
				serviceName = annotation.name();
			}

			NodeImpl scheduleNode = new NodeImpl();
			scheduleNode.addAttribute(EnvKey.Service.SERVICE_PATH, serviceDirectoryPath + "/" + serviceName);
			scheduleNode.addAttribute(EnvKey.Service.CRON, annotation.schedule());
			scheduleNode.addAttribute(EnvKey.Service.DUPLICATE_EXECUTION, Constant.NO);
			scheduleNode.addAttribute(EnvKey.Service.USE, Constant.YES);
			scheduleNode.addAttribute(EnvKey.Service.INIT_EXECUTION, Constant.NO);

			this.insertSchedulerInfo(serviceDirectoryPath, scheduleNode);
		}
	}
}
