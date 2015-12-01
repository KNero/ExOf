package balam.exof.listener.handler;

import java.nio.charset.Charset;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;

public class NullDelimiterStringCodec extends ChannelHandlerArray
{
	@Override
	public ChannelHandler[] make() 
	{
		ChannelHandler[] pipe = new ChannelHandler[]{
				new DelimiterBasedFrameDecoder(this.portInfo.getMaxLength(), Delimiters.nulDelimiter()),
				new StringDecoder(Charset.forName("UTF-8"))
		};
		
		return pipe;
	}
}
