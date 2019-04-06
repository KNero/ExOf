package team.balam.exof.environment.vo;

import io.netty.util.internal.StringUtil;
import team.balam.exof.Constant;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.environment.EnvKey;

public class PortInfo {
	private int number;
	private boolean isNull;

	public static PortInfo NULL_OBJECT = new PortInfo();

	public PortInfo(int _number) {
		this.number = _number;
	}

	private PortInfo() {
		this.isNull = true;
	}

	public int getNumber() {
		return number;
	}

	public final boolean isNull() {
		return isNull;
	}

	public String getSessionHandler() {
		return ListenerDao.selectChildAttribute(this.number, EnvKey.Listener.SESSION_HANDLER, EnvKey.Listener.CLASS);
	}

	public String getChannelHandler() {
		return ListenerDao.selectChildAttribute(this.number, EnvKey.Listener.CHANNEL_HANDLER, EnvKey.Listener.CLASS);
	}

	public String getMessageTransform() {
		return ListenerDao.selectChildAttribute(this.number, EnvKey.Listener.MESSAGE_TRANSFORM, EnvKey.Listener.CLASS);
	}

	public String getType() {
	    return ListenerDao.getPortType(this.number);
    }

	public boolean isSpecial() {
		return ListenerDao.isSpecialPort(this.number);
	}

	public String getAttribute(String _key) {
		return this.getAttribute(_key, Constant.EMPTY_STRING);
	}

	public String getAttribute(String _key, String _default) {
		String value = ListenerDao.selectPortAttribute(this.number, _key);
		if (!value.isEmpty()) {
			return value;
		} else {
			return _default;
		}
	}
	
	public int getAttributeToInt(String _key, int _default) {
		String value = ListenerDao.selectPortAttribute(this.number, _key);
		if (!value.isEmpty()) {
			return Integer.valueOf(value);
		} else {
			return _default;
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ListenerDao.selectAllPortAttribute(this.number));

		String sessionHandler = this.getSessionHandler();
		if (!StringUtil.isNullOrEmpty(sessionHandler)) {
			str.append("\nSession Handler : ").append(sessionHandler);
		}

		String channelHandler = this.getChannelHandler();
		if (!StringUtil.isNullOrEmpty(channelHandler)) {
			str.append("\nChannel Handler : ").append(channelHandler);
		}

		String messageTransform = this.getMessageTransform();
		if (!StringUtil.isNullOrEmpty(messageTransform)) {
			str.append("\nMessage Transform : ").append(messageTransform);
		}
		
		return str.toString();
	}
}
