package team.balam.exof.module.listener;

import java.util.Properties;

public class PortInfo
{
	private int number;
	
	private String sessionHandler;
	private String channelHandler;
	private String messageTransform;
	
	private Properties attr = new Properties();
	
	public PortInfo(int _number)
	{
		this.number = _number;
	}
	
	public int getNumber()
	{
		return this.number;
	}
	public String getSessionHandler()
	{
		return sessionHandler;
	}

	public void setSessionHandler(String sessionHandler)
	{
		if(sessionHandler.length() > 0) this.sessionHandler = sessionHandler;
	}

	public String getChannelHandler()
	{
		return channelHandler;
	}

	public void setChannelHandler(String channelHandler)
	{
		if(channelHandler.length() > 0) this.channelHandler = channelHandler;
	}

	public String getMessageTransform()
	{
		return messageTransform;
	}

	public void setMessageTransform(String messageTransform)
	{
		if(messageTransform.length() > 0) this.messageTransform = messageTransform;
	}
	
	public void addAttribute(String _key, String _value)
	{
		this.attr.setProperty(_key, _value);
	}
	
	public String getAttribute(String _key)
	{
		return this.attr.getProperty(_key);
	}
	
	public String getAttribute(String _key, String _default)
	{
		String value = this.attr.getProperty(_key);
		
		if(value != null) return value;
		else return _default;
	}
	
	public int getAttributeToInt(String _key, int _default)
	{
		String value = this.attr.getProperty(_key);
		
		if(value != null) return Integer.parseInt(value);
		else return _default;
	}

	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder("Number[" + this.number + "]");
		
		for(Object key : this.attr.keySet())
		{
			str.append(" ").append(key).append("[").append(this.attr.get(key)).append("]");
		}
		
		if(this.sessionHandler != null) str.append("\nSession Handler : ").append(this.sessionHandler);
		if(this.channelHandler != null) str.append("\nChannel Handler : ").append(this.channelHandler);
		if(this.messageTransform != null) str.append("\nMessage Transform : ").append(this.messageTransform);
		
		return str.toString();
	}
}
