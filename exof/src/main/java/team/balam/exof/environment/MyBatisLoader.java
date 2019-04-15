package team.balam.exof.environment;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import team.balam.exof.db.DbSessionFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;

@Slf4j
public class MyBatisLoader implements Loader {
	@Override
	public void load(String _envPath) throws LoadEnvException {
		File mybatis = new File(_envPath + "/mybatis-config.xml");
		if (!mybatis.exists() || mybatis.length() == 0) {
		    log.warn("file not found or contents is empty. {}", mybatis.getAbsolutePath());
		    return;
        }

		try (BufferedReader reader = new BufferedReader(new FileReader(mybatis))) {
			InputSource inputSource = new InputSource(reader);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document document = factory.newDocumentBuilder().parse(inputSource);

			XPath xPath = XPathFactory.newInstance().newXPath();

			XPathExpression expression = xPath.compile("//environments");
			Node node = (Node) expression.evaluate(document, XPathConstants.NODE);

			loadSqlSessionFactory(mybatis, node, "default", true);

			expression = xPath.compile("//environments/environment");
			NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);

				loadSqlSessionFactory(mybatis, node, "id", false);
			}
		} catch (Exception e) {
			throw new LoadEnvException("Fail to load mybatis config file.", e);
		}
	}

	private static void loadSqlSessionFactory(File configFile, Node node, String attrName, boolean isDefault) throws IOException {
		if (node != null && node.getAttributes() != null) {
			NamedNodeMap attr = node.getAttributes();
			if (attr != null) {
				Node attrNode = attr.getNamedItem(attrName);
				if (attrNode != null && !StringUtil.isNullOrEmpty(attrNode.getNodeValue())) {

					try (InputStream inputStream = new FileInputStream(configFile)) {
						org.apache.ibatis.session.SqlSessionFactory factory =
								new SqlSessionFactoryBuilder().build(inputStream, attrNode.getNodeValue());

						if (isDefault) {
							DbSessionFactory.getInstance().setDefaultSqlSessionFactory(factory);
						} else {
							DbSessionFactory.getInstance().putSqlSessionFactory(attrNode.getNodeValue(), factory);
						}

						log.info("loaded datasource: {}", attrNode.getNodeValue());
						return;
					}
				}
			}
		}

		log.warn("mybatis-config.xml environments default attribute is null.");
	}
}
