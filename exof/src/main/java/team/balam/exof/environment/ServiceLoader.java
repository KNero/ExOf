package team.balam.exof.environment;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
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

public class ServiceLoader implements Loader
{
	private int scheduleCount;

	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		String filePath = _envPath + "/service.xml";
		if (new File(filePath).exists()) {
			this._loadServiceAndScheduler(filePath);
		} else {
			throw new LoadEnvException("Service file is not exists. " + filePath);
		}
	}

	private void _loadServiceAndScheduler(String _filePath) throws LoadEnvException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File(_filePath));

			Node servicesNode = doc.getFirstChild();
			if (this._equalsNodeName(servicesNode, EnvKey.Service.SERVICES)) {
				Node serviceNode = servicesNode.getFirstChild();

				while (serviceNode != null) {
					if (this._equalsNodeName(serviceNode, EnvKey.Service.SERVICE_DIRECTORY)) {
						this._insertServiceInfo(serviceNode);
					} else if (this._equalsNodeName(serviceNode, EnvKey.Service.SCHEDULER)) {
						String[] pathArr = _filePath.split("/");
						String fileName = pathArr[pathArr.length - 1];

						this._insertSchedulerInfo(fileName, serviceNode);
					} else if (this._equalsNodeName(serviceNode, EnvKey.Service.RESOURCE)) {
						String serviceFile = serviceNode.getAttributes().getNamedItem(EnvKey.Service.FILE).getNodeValue();
						if (!new File(serviceFile).exists()) {
							throw new FileNotFoundException("input file path : [" + serviceFile + "]");
						}

						this._loadServiceAndScheduler(serviceFile);
					} else if (this._equalsNodeName(serviceNode, EnvKey.Service.SERVICE_PACKAGE)) {
						this._scanServicePackage(serviceNode);
					}

					serviceNode = serviceNode.getNextSibling();
				}
			}
		} catch (Exception e) {
			throw new LoadEnvException(_filePath, e);
		}
	}
	
	private boolean _equalsNodeName(Node _node, String _name)
	{
		return _node != null && _name.equals(_node.getNodeName());
	}
	
	private void _insertServiceInfo(Node _node) throws LoadEnvException {
		NamedNodeMap attr = _node.getAttributes();
		String path = attr.getNamedItem(EnvKey.Service.PATH).getNodeValue();
		String className = attr.getNamedItem(EnvKey.Service.CLASS).getNodeValue();

		ServiceInfoDao.insertServiceDirectory(path, className);
		
		Node serviceVariableNode = _node.getFirstChild();
		while(serviceVariableNode != null) {
			if(this._equalsNodeName(serviceVariableNode, EnvKey.Service.SERVICE_VARIABLE)) {
				this._loadServiceVariable(path, serviceVariableNode);
			}
			
			serviceVariableNode = serviceVariableNode.getNextSibling();
		}
	}

	private void _loadServiceVariable(String _directoryPath, Node _serviceVariableNode) throws LoadEnvException {
		NamedNodeMap serviceVariableAttribute = _serviceVariableNode.getAttributes();
		String serviceName = serviceVariableAttribute.getNamedItem(EnvKey.Service.SERVICE_NAME).getNodeValue();

		Node variableNode = _serviceVariableNode.getFirstChild();
		while(variableNode != null) {
			if(this._equalsNodeName(variableNode, EnvKey.Service.VARIABLE)) {
				NamedNodeMap variAttr = variableNode.getAttributes();
				String name = variAttr.getNamedItem(EnvKey.Service.NAME).getNodeValue();
				String value = variAttr.getNamedItem(EnvKey.Service.VALUE).getNodeValue();

				ServiceInfoDao.insertServiceVariable(_directoryPath, serviceName, name, value);
			}

			variableNode = variableNode.getNextSibling();
		}
	}
	
	/**
	 * xml의 정보를 통해 스케쥴러를 생성한다.
	 * @param _fileName 스케쥴러 자동 아이디 생성을 위해서 prefix로 사용된다.
	 * @param _node 스케쥴러 정보를 갖고있는 xml node
	 */
	private void _insertSchedulerInfo(String _fileName, Node _node) throws LoadEnvException {
		NamedNodeMap attr = _node.getAttributes();

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
			id = _fileName + "-scheduler-" + this.scheduleCount++;
		}

		try {
			if (ServiceInfoDao.selectScheduler(id).isNull()) {
				ServiceInfoDao.insertSchedule(id, servicePath, cron, isDuplicateExecution, isUse, isInitExecution);
			}
		} catch (Exception e) {
			throw new LoadEnvException("service.xml", e);
		}
	}

	private void _scanServicePackage(Node _servicePackageNode) throws LoadEnvException {
		Node attribute = _servicePackageNode.getAttributes().getNamedItem(EnvKey.Service.PACKAGE);
		if (attribute != null) {
			String packageName = attribute.getNodeValue();
			Reflections reflections = new Reflections(new ConfigurationBuilder()
					.addClassLoader(ExternalClassLoader.getClassLoader())
					.setUrls(ClasspathHelper.forPackage(packageName, ExternalClassLoader.getClassLoader())));

			Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(ServiceDirectory.class);
			for (Class<?> serviceDirectory : classSet) {
				Package classPackage = serviceDirectory.getPackage();
				if (classPackage != null && packageName.equals(classPackage.getName())) {
					ServiceDirectory annotation = serviceDirectory.getAnnotation(ServiceDirectory.class);

					if (!annotation.value().isEmpty()) {
						ServiceInfoDao.insertServiceDirectory(annotation.value(), serviceDirectory.getName());

						Method[] methods = serviceDirectory.getMethods();
						for (Method method : methods) {
							this._insertScheduleFromAutoScan(annotation.value(), method);
						}
					} else {
						throw new LoadEnvException("servicePackage's service directory must have path in ServiceDirectory annotation. "
								+ serviceDirectory.getName());
					}
				}
			}
		}
	}

	private void _insertScheduleFromAutoScan(String _serviceDirectoryPath, Method _method) throws LoadEnvException {
		Service annotation = _method.getAnnotation(Service.class);
		if (annotation != null && !annotation.schedule().isEmpty()) {
			String serviceName = _method.getName();
			if (!annotation.name().isEmpty()) {
				serviceName = annotation.name();
			}

			NodeImpl scheduleNode = new NodeImpl();
			scheduleNode.addAttribute(EnvKey.Service.SERVICE_PATH, _serviceDirectoryPath + "/" + serviceName);
			scheduleNode.addAttribute(EnvKey.Service.CRON, annotation.schedule());
			scheduleNode.addAttribute(EnvKey.Service.DUPLICATE_EXECUTION, Constant.NO);
			scheduleNode.addAttribute(EnvKey.Service.USE, Constant.YES);
			scheduleNode.addAttribute(EnvKey.Service.INIT_EXECUTION, Constant.NO);

			this._insertSchedulerInfo(_serviceDirectoryPath, scheduleNode);
		}
	}
}
