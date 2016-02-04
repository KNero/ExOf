package balam.exof.listener.handler;

import io.netty.channel.ChannelHandler;
import balam.exof.listener.PortInfo;

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
