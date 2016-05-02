package team.balam.exof.listener.handler;

import team.balam.exof.listener.PortInfo;
import io.netty.channel.ChannelHandler;

public abstract class ChannelHandlerArray
{
	protected int maxLength;
	
	public ChannelHandlerArray()
	{
		
	}
	
	public void setMaxLength(int _length)
	{
		this.maxLength = _length;
	}
	
	public abstract void init(PortInfo _info);
	
	public abstract ChannelHandler[] make();
}
