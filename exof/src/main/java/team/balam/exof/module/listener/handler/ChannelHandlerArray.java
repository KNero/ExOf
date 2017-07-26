package team.balam.exof.module.listener.handler;

import team.balam.exof.module.listener.PortInfo;

public interface ChannelHandlerArray extends ChannelHandlerMaker
{
	void init(PortInfo _info);
	
	void destroy();
}
