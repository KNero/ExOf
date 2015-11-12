package balam.exof.environment;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import balam.exof.scheduler.SchedulerInfo;
import balam.exof.service.ServiceDirectoryInfo;

public class ServiceLoader implements Loader
{
	@Override
	public void load(String _envPath) throws LoadEnvException 
	{
		String serviceFileName = "service.xml";
		
		List<ServiceDirectoryInfo> serviceDirectoryList = new LinkedList<>();
		SystemSetting.getInstance().set(EnvKey.PreFix.SERVICE, EnvKey.Service.SERVICE, serviceDirectoryList);
		
		List<SchedulerInfo> schedulerList = new LinkedList<>();
		SystemSetting.getInstance().set(EnvKey.PreFix.SERVICE, EnvKey.Service.SCHEDULE, schedulerList);
		
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File(_envPath + "/" + serviceFileName));
			
			Node servicesNode = doc.getFirstChild();
			if(this._equalsNodeName(servicesNode, "services"))
			{
				Node categoryNode = servicesNode.getFirstChild();
				while(categoryNode != null)
				{
					if(this._equalsNodeName(categoryNode, "category"))
					{
						Node serviceNode = categoryNode.getFirstChild();
						while(serviceNode != null)
						{
							if(this._equalsNodeName(serviceNode, "serviceDirectory"))
							{
								ServiceDirectoryInfo info = this._makeServiceDirectory(serviceNode);
								serviceDirectoryList.add(info);
							}
							else if(this._equalsNodeName(serviceNode, "scheduler"))
							{
								SchedulerInfo info = this._makeSchedulerInfo(serviceNode);
								schedulerList.add(info);
							}
							
							serviceNode = serviceNode.getNextSibling();
						}
					}
					
					categoryNode = categoryNode.getNextSibling();
				}
			}
		}
		catch(Exception e)
		{
			throw new LoadEnvException(serviceFileName, e);
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
		info.setClassName(attr.getNamedItem("class").getNodeValue());
		info.setPath(attr.getNamedItem("path").getNodeValue());
		
		Node serviceVariableNode = _node.getFirstChild();
		while(serviceVariableNode != null)
		{
			if(this._equalsNodeName(serviceVariableNode, "serviceVariale"))
			{
				NamedNodeMap serVariAttr = serviceVariableNode.getAttributes();
				String serviceName = serVariAttr.getNamedItem("serviceName").getNodeValue();
				LinkedHashMap<String, String> variable = new LinkedHashMap<>();
				
				info.setVariable(serviceName, variable);
				
				Node variableNode = serviceVariableNode.getFirstChild();
				while(variableNode != null)
				{
					if(this._equalsNodeName(variableNode, "variable"))
					{
						NamedNodeMap variAttr = variableNode.getAttributes();
						String name = variAttr.getNamedItem("name").getNodeValue();
						String value = variAttr.getNamedItem("value").getNodeValue();
						
						variable.put(name, value);
					}
					
					variableNode = variableNode.getNextSibling();
				}
			}
			
			serviceVariableNode = serviceVariableNode.getNextSibling();
		}
		
		return info;
	}
	
	private SchedulerInfo _makeSchedulerInfo(Node _node)
	{
		NamedNodeMap attr = _node.getAttributes();
		
		SchedulerInfo info = new SchedulerInfo();
		info.setServicePath(attr.getNamedItem("servicePath").getNodeValue());
		info.setCronExpression(attr.getNamedItem("cron").getNodeValue());
		info.setDuplicateExecution("yes".equals(attr.getNamedItem("duplicateExecution").getNodeValue()));
		info.setUse("yes".equals(attr.getNamedItem("use").getNodeValue()));
		
		return info;
	}
}
