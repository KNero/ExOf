package team.balam.exof.test;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.handler.SessionEventHandler;

public class TestSessionHandler implements SessionEventHandler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void openedSession(ChannelHandlerContext _ctx) {
		this.logger.info("Opened session. " + _ctx);
	}

	@Override
	public void closedSession(ChannelHandlerContext _ctx) {
		this.logger.info("closed session. " + _ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext _ctx, Throwable _cause) {
		this.logger.error("session error. " + _ctx, _cause);
	}
}
