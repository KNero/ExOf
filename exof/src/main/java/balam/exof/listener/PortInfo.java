package balam.exof.listener;

public class PortInfo
{
	private int number;
	private int maxLength;
	private int lengthOffset;
	private int lengthSize;
	
	private String sessionHandler;
	private String channelHandler;
	private String messageTransform;
	
	public PortInfo(int _number)
	{
		this.number = _number;
	}
	
	public int getNumber()
	{
		return this.number;
	}
	
	public int getMaxLength()
	{
		return maxLength;
	}

	public void setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
	}

	public int getLengthOffset()
	{
		return lengthOffset;
	}

	public void setLengthOffset(int legthOffset)
	{
		this.lengthOffset = legthOffset;
	}

	public int getLengthSize()
	{
		return lengthSize;
	}

	public void setLengthSize(int lengthSize)
	{
		if(lengthSize > 0) this.lengthSize = lengthSize;
	}

	public String getSessionHandler()
	{
		return sessionHandler;
	}

	public void setSessionHandler(String sessionHandler)
	{
		if(sessionHandler.trim().length() > 0) this.sessionHandler = sessionHandler;
	}

	public String getChannelHandler()
	{
		return channelHandler;
	}

	public void setChannelHandler(String channelHandler)
	{
		if(channelHandler.trim().length() > 0) this.channelHandler = channelHandler;
	}

	public String getMessageTransform()
	{
		return messageTransform;
	}

	public void setMessageTransform(String messageTransform)
	{
		if(messageTransform.trim().length() > 0) this.messageTransform = messageTransform;
	}

	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder("Port Info : " + this.number);
		str.append("\nMax Length : ").append(this.maxLength);
		str.append("\nLegth Offset : ").append(this.lengthOffset);
		str.append("\nLength Size : ").append(this.lengthSize);
		
		if(this.sessionHandler != null) str.append("\nSession Handler : ").append(this.sessionHandler);
		if(this.channelHandler != null) str.append("\nChannel Handler : ").append(this.channelHandler);
		if(this.messageTransform != null) str.append("\nMessage Transform : ").append(this.messageTransform);
		
		return str.toString();
	}
}
