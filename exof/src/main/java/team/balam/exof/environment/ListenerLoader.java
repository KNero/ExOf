package team.balam.exof.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import team.balam.exof.Constant;
import team.balam.exof.db.ListenerDao;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ListenerLoader implements Loader {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void load(String _envPath) throws LoadEnvException
	{
		String filePath = _envPath + "/listener.xml";
		File listenerFile = new File(filePath);
		if (!listenerFile.exists() || listenerFile.length() == 0) {
			logger.warn("file not found or contents is empty. {}", listenerFile.getAbsolutePath());
			return;
		}
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(listenerFile);
			
			Node listenerNode = doc.getFirstChild();
			if(this._equalsNodeName(listenerNode, EnvKey.Listener.LISTENER)) {
				Node portNode = listenerNode.getFirstChild();

				while(portNode != null) {

					if(this._equalsNodeName(portNode, EnvKey.Listener.PORT)) {
						int port = this.findNumber(portNode);
						if (port > 0) {
							this.insertPortAttributes(portNode, port);

							Node portChildNode = portNode.getFirstChild();
							while (portChildNode != null) {
								if (portChildNode.getNodeType() == Node.ELEMENT_NODE) {
									String className = this.findAttribute(portChildNode, EnvKey.Listener.CLASS);
									if (className.isEmpty()) {
										logger.error("Class is empty. {}", portChildNode.getNodeName());
									} else {
										if (this._equalsNodeName(portChildNode, EnvKey.Listener.SESSION_HANDLER)) {
											ListenerDao.insertChildNode(port, EnvKey.Listener.SESSION_HANDLER, EnvKey.Listener.CLASS, className);
										} else if (this._equalsNodeName(portChildNode, EnvKey.Listener.CHANNEL_HANDLER)) {
											ListenerDao.insertChildNode(port, EnvKey.Listener.CHANNEL_HANDLER, EnvKey.Listener.CLASS, className);
										} else if (this._equalsNodeName(portChildNode, EnvKey.Listener.MESSAGE_TRANSFORM)) {
											ListenerDao.insertChildNode(port, EnvKey.Listener.MESSAGE_TRANSFORM, EnvKey.Listener.CLASS, className);
										}
									}
								}
								portChildNode = portChildNode.getNextSibling();
							}
						}
					}
					portNode = portNode.getNextSibling();
				}
			}
		} catch(Exception e) {
			throw new LoadEnvException(filePath, e);
		}
	}

	/**
	 * jetty 설정의 경우 http 혹은 https 로 읽어 온다.
	 * @param _node
	 * @return
	 */
	private int findNumber(Node _node) {
		String value = this.findAttribute(_node, EnvKey.Listener.NUMBER);
		if (value.isEmpty()) {
			value = this.findAttribute(_node, EnvKey.Listener.HTTP);

			if (value.isEmpty()) {
				value = this.findAttribute(_node, EnvKey.Listener.HTTPS);
			}
		}

		int port = 0;
		try {
			port = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			this.logger.error("Port number is wrong.", e);
		}

		return port;
	}

	private String findAttribute(Node _node, String _key) {
		NamedNodeMap attr =  _node.getAttributes();

		for(int a = 0; a < attr.getLength(); ++a) {
			Node attrNode = attr.item(a);
			String attrName = attrNode.getNodeName();

			if (_key.equals(attrName)) {
				return attrNode.getNodeValue();
			}
		}

		return Constant.EMPTY_STRING;
	}

	private void insertPortAttributes(Node _node, int _port) throws LoadEnvException {
		NamedNodeMap attr =  _node.getAttributes();

		for(int a = 0; a < attr.getLength(); ++a) {
			Node attrNode = attr.item(a);
			String attrName = attrNode.getNodeName();
			String attrValue = attrNode.getNodeValue();

			ListenerDao.insertPortAttribute(_port, attrName, attrValue);
		}
	}
	
	private boolean _equalsNodeName(Node _node, String _name)
	{
		return _node != null && _name.equals(_node.getNodeName());
	}
}
