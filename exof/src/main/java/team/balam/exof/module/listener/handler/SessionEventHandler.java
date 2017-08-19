package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandlerContext;
import team.balam.exof.environment.vo.PortInfo;

public interface SessionEventHandler
{
	default void init(PortInfo _portInfo)
	{
		
	}
	
	void openedSession(ChannelHandlerContext _ctx);
	
	void closedSession(ChannelHandlerContext _ctx);
	
	void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}
