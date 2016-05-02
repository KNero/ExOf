package team.balam.exof.listener.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

import team.balam.exof.ConstantKey;
import team.balam.exof.listener.PortInfo;

public class NullDelimiterStringCodec extends ChannelHandlerArray
{
	@Override
	public void init(PortInfo _info) 
	{
		
	}
	
	@Override
	public ChannelHandler[] make() 
	{
		ChannelHandler[] pipe = new ChannelHandler[]{
				new DelimiterBasedFrameDecoder(this.maxLength, Delimiters.nulDelimiter()),
				new StringDecoder(Charset.forName(ConstantKey.NETWORK_CHARSET))
		};
		
		return pipe;
	}
}
