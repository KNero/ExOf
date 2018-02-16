package team.balam.exof.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

public class ResponseFutureImpl extends ChannelInboundHandlerAdapter implements ResponseFuture {
	private Object response;
	private Object exception;
	
	ResponseFutureImpl() {

	}
	
	@Override
	public void channelRead(ChannelHandlerContext _ctx, Object _msg) {
		this.response = _msg;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if(cause instanceof Exception) {
			this.exception = cause;
		} else {
			this.exception = new Exception(cause);
		}
	}

	@Override
	public void await(long _timeoutMillis) {
		long start = System.currentTimeMillis();

		while (true) {
			if (this.response != null || this.exception != null) {
				break;
			} else if (System.currentTimeMillis() - start >= _timeoutMillis) {
				this.exception = new IOException("Read Timeout.");
				break;
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				//ignore
			}
		}
	}
	
	@Override
	public boolean isDone() {
		return this.response != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get() throws Exception {
		if (this.response != null) {
			return (T) this.response;
		} else if (this.exception != null) {
			throw (Exception) this.exception;
		} else {
			return null;
		}
	}
}
