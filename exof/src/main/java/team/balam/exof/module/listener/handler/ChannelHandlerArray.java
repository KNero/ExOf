package team.balam.exof.module.listener.handler;

import team.balam.exof.environment.vo.PortInfo;

public interface ChannelHandlerArray extends ChannelHandlerMaker {
	default void init(PortInfo info) {

	}
	
	default void destroy() {

	}
}
