package team.balam.exof.client.component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContextBuilder;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;
import team.balam.exof.module.listener.handler.ChannelInitializerException;

import javax.net.ssl.SSLException;

public class HttpsClientCodec implements ChannelHandlerMaker {
	@Override
	public ChannelHandler[] make(SocketChannel socketChannel) throws ChannelInitializerException {
		try {
			return new ChannelHandler[]{
					SslContextBuilder.forClient().build().newHandler(socketChannel.alloc()),
					new io.netty.handler.codec.http.HttpClientCodec(),
					new HttpObjectAggregator(Integer.MAX_VALUE)};
		} catch (SSLException e) {
			throw new ChannelInitializerException("Fail to init SSL.", e);
		}
	}
}
