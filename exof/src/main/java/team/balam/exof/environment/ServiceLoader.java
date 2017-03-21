package team.balam.exof.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import team.balam.exof.Constant;
import team.balam.exof.container.scheduler.SchedulerInfo;
import team.balam.exof.module.service.ServiceDirectoryInfo;

public class ServiceLoader implements Loader
{
	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		List<ServiceDirectoryInfo> serviceDirectoryList = new LinkedList<>();
		SystemSetting.getInstance().set(EnvKey.PreFix.SERVICE, EnvKey.Service.SERVICES, serviceDirectoryList);
		
		List<SchedulerInfo> schedulerList = new LinkedList<>();
		SystemSetting.getInstance().set(EnvKey.PreFix.SERVICE, EnvKey.Service.SCHEDULER, schedulerList);
		
		this._loadServiceAndScheduler(_envPath + "/service.xml", serviceDirectoryList, schedulerList);
	}
	
	private void _loadServiceAndScheduler(String _filePath, 
			List<ServiceDirectoryInfo> _serviceDirectoryList, List<SchedulerInfo> _schedulerList) throws LoadEnvException
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File( _filePath));
			
			Node servicesNode = doc.getFirstChild();
			if(this._equalsNodeName(servicesNode, EnvKey.Service.SERVICES))
			{
				int idIndex = 0;
				
				Node serviceNode = servicesNode.getFirstChild();
				while(serviceNode != null)
				{
					if(this._equalsNodeName(serviceNode, EnvKey.Service.SERVICE_DIRECTORY))
					{
						ServiceDirectoryInfo info = this._makeServiceDirectory(serviceNode);
						_serviceDirectoryList.add(info);
					}
					else if(this._equalsNodeName(serviceNode, EnvKey.Service.SCHEDULER))
					{
						String[] pathArr = _filePath.split("/");
						String fileName = pathArr[pathArr.length - 1];
						
						SchedulerInfo info = this._makeSchedulerInfo(fileName, idIndex++, serviceNode);
						_schedulerList.add(info);
					}
					else if(this._equalsNodeName(serviceNode, EnvKey.Service.RESOURCE))
					{
						String serviceFile = serviceNode.getAttributes().getNamedItem(EnvKey.Service.FILE).getNodeValue();
						File file = new File(serviceFile);
						if(! file.exists())
						{
							throw new FileNotFoundException("input file path : [" + serviceFile + "]");
						}
						
						this._loadServiceAndScheduler(serviceFile, _serviceDirectoryList, _schedulerList);
					}
					
					serviceNode = serviceNode.getNextSibling();
				}
			}
		}
		catch(Exception e)
		{
			throw new LoadEnvException(_filePath, e);
		}
	}
	
	private boolean _equalsNodeName(Node _node, String _name)
	{
		return _node != null && _name.equals(_node.getNodeName());
	}
	
	private ServiceDirectoryInfo _makeServiceDirectory(Node _node)
	{
		NamedNodeMap attr = _node.getAttributes();
		
		ServiceDirectoryInfo info = new ServiceDirectoryInfo();
		info.setClassName(attr.getNamedItem(EnvKey.Service.CLASS).getNodeValue());
		info.setPath(attr.getNamedItem(EnvKey.Service.PATH).getNodeValue());
		
		Node serviceVariableNode = _node.getFirstChild();
		while(serviceVariableNode != null)
		{
			if(this._equalsNodeName(serviceVariableNode, EnvKey.Service.SERVICE_VARIABLE))
			{
				NamedNodeMap serVariAttr = serviceVariableNode.getAttributes();
				String serviceName = serVariAttr.getNamedItem(EnvKey.Service.SERVICE_NAME).getNodeValue();
				LinkedHashMap<String, String> variable = new LinkedHashMap<>();
				
				info.setVariable(serviceName, variable);
				
				Node variableNode = serviceVariableNode.getFirstChild();
				while(variableNode != null)
				{
					if(this._equalsNodeName(variableNode, EnvKey.Service.VARIABLE))
					{
						NamedNodeMap variAttr = variableNode.getAttributes();
						String name = variAttr.getNamedItem(EnvKey.Service.NAME).getNodeValue();
						String value = variAttr.getNamedItem(EnvKey.Service.VALUE).getNodeValue();
						
						variable.put(name, value);
					}
					
					variableNode = variableNode.getNextSibling();
				}
			}
			
			serviceVariableNode = serviceVariableNode.getNextSibling();
		}
		
		return info;
	}
	
	/**
	 * xml의 정보를 통해 스케쥴러를 생성한다.
	 * @param _fileName 스케쥴러 자동 아이디 생성을 위해서 prefix로 사용된다.
	 * @param _count 스케쥴러 자동 아이디 생성을 위해서 prefix로 사용된다.
	 * @param _node 스케쥴러 정보를 갖고있는 xml node
	 * @return 생성된 스케쥴러 정보 객체
	 */
	private SchedulerInfo _makeSchedulerInfo(String _fileName, int _count, Node _node)
	{
		NamedNodeMap attr = _node.getAttributes();
		
		SchedulerInfo info = new SchedulerInfo();
		info.setServicePath(attr.getNamedItem("servicePath").getNodeValue());
		info.setCronExpression(attr.getNamedItem("cron").getNodeValue());
		info.setDuplicateExecution(Constant.YES.equals(attr.getNamedItem("duplicateExecution").getNodeValue()));
		info.setUse(Constant.YES.equals(attr.getNamedItem("use").getNodeValue()));
		info.setInitExecution(Constant.YES.equals(attr.getNamedItem("initExecution").getNodeValue()));
		
		String id = null;
		Node idAttr = attr.getNamedItem("id"); 
		if(idAttr != null && idAttr.getNodeValue().trim().length() > 0)
		{
			id = idAttr.getNodeValue();
		}
		else
		{
			id = _fileName + "-scheduler-" + _count;
		}
		
		info.setId(id);
		
		return info;
	}
}
