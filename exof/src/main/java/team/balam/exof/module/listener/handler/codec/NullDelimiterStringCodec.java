package team.balam.exof.module.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

import team.balam.exof.ConstantKey;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class NullDelimiterStringCodec extends ChannelHandlerArray
{
	@Override
	public void init(PortInfo _info) 
	{
		
	}
	
	@Override
	public void destroy() 
	{
		
	}
	
	@Override
	public ChannelHandler[] make(SocketChannel _socketChannel) 
	{
		ChannelHandler[] pipe = new ChannelHandler[]{
				new DelimiterBasedFrameDecoder(this.maxLength, Delimiters.nulDelimiter()),
				new StringDecoder(Charset.forName(ConstantKey.NETWORK_CHARSET)),
				new StringEncoder(Charset.forName(ConstantKey.NETWORK_CHARSET))
		};
		
		return pipe;
	}
}
