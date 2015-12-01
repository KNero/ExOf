package balam.exof.listener.handler;

import io.netty.channel.ChannelHandler;
import balam.exof.listener.PortInfo;

public abstract class ChannelHandlerArray
{
	protected PortInfo portInfo;
	
	public void setPortInfo(PortInfo _info)
	{
		this.portInfo = _info;
	}
	
	public abstract ChannelHandler[] make();
}
