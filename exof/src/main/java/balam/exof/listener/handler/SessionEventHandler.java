package balam.exof.listener.handler;

import io.netty.channel.ChannelHandlerContext;

public interface SessionEventHandler
{
	void openedSession(ChannelHandlerContext _ctx) throws Exception;
	
	void closedSession(ChannelHandlerContext _ctx) throws Exception;
	
	void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}
