package team.balam.exof.module.listener.handler;

import team.balam.exof.environment.vo.PortInfo;

public interface ChannelHandlerArray extends ChannelHandlerMaker
{
	void init(PortInfo _info);
	
	void destroy();
}
