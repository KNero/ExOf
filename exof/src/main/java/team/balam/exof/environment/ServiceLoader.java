package team.balam.exof.environment;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import team.balam.exof.db.ServiceInfoDao;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;

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

	private void _loadServiceAndScheduler(String _filePath)
			throws LoadEnvException {
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
}
