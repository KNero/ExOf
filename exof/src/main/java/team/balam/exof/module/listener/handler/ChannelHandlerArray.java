package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import team.balam.exof.module.listener.PortInfo;

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
	
	public abstract void destroy();
	
	public abstract ChannelHandler[] make(SocketChannel _socketChannel);
}
