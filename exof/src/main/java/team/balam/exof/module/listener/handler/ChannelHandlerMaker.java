package team.balam.exof.module.listener.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;

public interface ChannelHandlerMaker {
	ChannelHandler[] make(SocketChannel socketChannel) throws ChannelInitializerException;
}
