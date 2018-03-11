package team.balam.exof.client.component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

public class HttpClientCodec implements ChannelHandlerMaker {
	private int maxContentLength;

	public HttpClientCodec(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public HttpClientCodec() {
		this.maxContentLength = Integer.MAX_VALUE;
	}

	@Override
	public ChannelHandler[] make(SocketChannel socketChannel) {
		return new ChannelHandler[]{
				new io.netty.handler.codec.http.HttpClientCodec(),
				new HttpObjectAggregator(this.maxContentLength)};
	}
}
