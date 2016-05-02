package team.balam.exof.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import team.balam.exof.listener.PortInfo;
import team.balam.exof.listener.handler.ChannelHandlerArray;

public class HttpServerCodec extends ChannelHandlerArray
{
	@Override
	public void init(PortInfo _info) 
	{
		
	}

	@Override
	public ChannelHandler[] make() 
	{
		return new ChannelHandler[]{new HttpRequestDecoder(), new HttpResponseEncoder()};
	}

}
