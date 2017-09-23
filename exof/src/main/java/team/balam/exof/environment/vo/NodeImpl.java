package team.balam.exof.environment.vo;

import com.sun.org.apache.xerces.internal.impl.xs.opti.NamedNodeMapImpl;
import org.w3c.dom.*;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.List;

public class NodeImpl implements Node {
	private List<Attr> attributes = new ArrayList<>();

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public void setValue(String value) {

	}

	@Override
	public void setParentElement(SOAPElement parent) throws SOAPException {

	}

	@Override
	public SOAPElement getParentElement() {
		return null;
	}

	@Override
	public void detachNode() {

	}

	@Override
	public void recycleNode() {

	}

	@Override
	public String getNodeName() {
		return null;
	}

	@Override
	public String getNodeValue() throws DOMException {
		return null;
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {

	}

	@Override
	public short getNodeType() {
		return 0;
	}

	@Override
	public org.w3c.dom.Node getParentNode() {
		return null;
	}

	@Override
	public NodeList getChildNodes() {
		return null;
	}

	@Override
	public org.w3c.dom.Node getFirstChild() {
		return null;
	}

	@Override
	public org.w3c.dom.Node getLastChild() {
		return null;
	}

	@Override
	public org.w3c.dom.Node getPreviousSibling() {
		return null;
	}

	@Override
	public org.w3c.dom.Node getNextSibling() {
		return null;
	}

	public void addAttribute(String _key, String _value) {
		this.attributes.add(new AttrImpl(_key, _value));
	}

	@Override
	public NamedNodeMap getAttributes() {
		Attr[] attrs = new Attr[this.attributes.size()];
		for (int i = 0; i < this.attributes.size(); ++i) {
			attrs[i] = this.attributes.get(i);
		}
		return new NamedNodeMapImpl(attrs);
	}

	@Override
	public Document getOwnerDocument() {
		return null;
	}

	@Override
	public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
		return null;
	}

	@Override
	public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
		return null;
	}

	@Override
	public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
		return null;
	}

	@Override
	public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
		return null;
	}

	@Override
	public boolean hasChildNodes() {
		return false;
	}

	@Override
	public org.w3c.dom.Node cloneNode(boolean deep) {
		return null;
	}

	@Override
	public void normalize() {

	}

	@Override
	public boolean isSupported(String feature, String version) {
		return false;
	}

	@Override
	public String getNamespaceURI() {
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {

	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public boolean hasAttributes() {
		return false;
	}

	@Override
	public String getBaseURI() {
		return null;
	}

	@Override
	public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
		return 0;
	}

	@Override
	public String getTextContent() throws DOMException {
		return null;
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {

	}

	@Override
	public boolean isSameNode(org.w3c.dom.Node other) {
		return false;
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		return null;
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return false;
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return null;
	}

	@Override
	public boolean isEqualNode(org.w3c.dom.Node arg) {
		return false;
	}

	@Override
	public Object getFeature(String feature, String version) {
		return null;
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return null;
	}

	@Override
	public Object getUserData(String key) {
		return null;
	}

	private class AttrImpl extends NodeImpl implements Attr {
		private String name;
		private String value;

		private AttrImpl(String _name, String _value) {
			this.name = _name;
			this.value = _value;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getValue() {
			return this.value;
		}

		@Override
		public String getNodeValue() throws DOMException {
			return this.value;
		}

		@Override
		public boolean getSpecified() {
			return false;
		}

		@Override
		public Element getOwnerElement() {
			return null;
		}

		@Override
		public TypeInfo getSchemaTypeInfo() {
			return null;
		}

		@Override
		public boolean isId() {
			return false;
		}
	}
}
