package team.balam.exof.client.component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class HttpClientCodec implements ChannelHandlerArray{
	private int maxContentLength;

	public HttpClientCodec(int _maxContentLength) {
		this.maxContentLength = _maxContentLength;
	}

	public HttpClientCodec() {
		this.maxContentLength = Integer.MAX_VALUE;
	}

	@Override
	public void init(PortInfo _info) {

	}

	@Override
	public ChannelHandler[] make(SocketChannel _socketChannel) {
		return new ChannelHandler[]{
				new io.netty.handler.codec.http.HttpClientCodec(),
				new HttpObjectAggregator(this.maxContentLength)};
	}

	@Override
	public void destroy() {

	}
}
