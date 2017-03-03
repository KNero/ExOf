package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandlerContext;
import team.balam.exof.module.listener.PortInfo;

public interface SessionEventHandler
{
	default void init(PortInfo _portInfo) throws Exception
	{
		
	}
	
	void openedSession(ChannelHandlerContext _ctx) throws Exception;
	
	void closedSession(ChannelHandlerContext _ctx) throws Exception;
	
	void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}
