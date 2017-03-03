package team.balam.exof.module.listener.handler;

import team.balam.exof.module.listener.PortInfo;

public abstract class ChannelHandlerArray implements ChannelHandlerMaker
{
//	protected int maxLength;
	
//	public void setMaxLength(int _length)
//	{
//		this.maxLength = _length;
//	}
	
	public abstract void init(PortInfo _info);
	
	public abstract void destroy();
}
