package team.balam.exof.module.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

import team.balam.exof.Constant;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class NullDelimiterStringCodec implements ChannelHandlerArray
{
	private int maxLength;
	
	@Override
	public void init(PortInfo info)
	{
		this.maxLength = info.getAttributeToInt(EnvKey.Listener.MAX_LENGTH, 1024 * 8);
	}
	
	@Override
	public ChannelHandler[] make(SocketChannel socketChannel)
	{
		return new ChannelHandler[]{
				new DelimiterBasedFrameDecoder(this.maxLength, Delimiters.nulDelimiter()),
				new StringDecoder(Charset.forName(Constant.NETWORK_CHARSET)),
				new StringEncoder(Charset.forName(Constant.NETWORK_CHARSET))
		};
	}
}
