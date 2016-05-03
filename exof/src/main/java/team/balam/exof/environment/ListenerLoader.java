package team.balam.exof.environment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import team.balam.exof.listener.PortInfo;

public class ListenerLoader implements Loader
{
	@Override
	public void load(String _envPath) throws LoadEnvException
	{
		List<PortInfo> portList = new LinkedList<>();
		SystemSetting.getInstance().set(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT, portList);
		
		String filePath = _envPath + "/listener.xml";
		
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File(filePath));
			
			Node listenerNode = doc.getFirstChild();
			if(this._equalsNodeName(listenerNode, "listener"))
			{
				Node portNode = listenerNode.getFirstChild();
				while(portNode != null)
				{
					if(this._equalsNodeName(portNode, "port"))
					{
						NamedNodeMap attr =  portNode.getAttributes();
						String number = attr.getNamedItem("number").getNodeValue();
						
						PortInfo info = new PortInfo(Integer.parseInt(number));
						
						for(int a = 0; a < attr.getLength(); ++a)
						{
							Node attrNode = attr.item(a);
							String attrName = attrNode.getNodeName();
							String attrValue = attrNode.getNodeValue();
							
							info.addAttribute(attrName, attrValue);
							
						}
						
						Node portChildNode = portNode.getFirstChild();
						while(portChildNode != null)
						{
							if(portChildNode.getNodeType() == Node.ELEMENT_NODE)
							{
								NamedNodeMap childAttr = portChildNode.getAttributes();
								Node classAttr = childAttr.getNamedItem("class");
								
								if(classAttr != null)
								{
									String className = classAttr.getNodeValue().trim();
									
									if(this._equalsNodeName(portChildNode, "sessionHandler"))
									{
										info.setSessionHandler(className);
									}
									else if(this._equalsNodeName(portChildNode, "channelHandler"))
									{
										info.setChannelHandler(className);
									}
									else if(this._equalsNodeName(portChildNode, "messageTransform"))
									{
										info.setMessageTransform(className);
									}
								}
							}
							
							portChildNode = portChildNode.getNextSibling();
						}
						
						portList.add(info);
					}
					
					portNode = portNode.getNextSibling();
				}
			}
		}
		catch(Exception e)
		{
			throw new LoadEnvException(filePath, e);
		}
	}
	
	private boolean _equalsNodeName(Node _node, String _name)
	{
		return _node != null && _name.equals(_node.getNodeName());
	}
}