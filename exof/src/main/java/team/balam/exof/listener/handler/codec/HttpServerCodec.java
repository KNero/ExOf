package team.balam.exof.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.listener.PortInfo;
import team.balam.exof.listener.handler.ChannelHandlerArray;

public class HttpServerCodec extends ChannelHandlerArray
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SslContext sslCtx;
	
	@Override
	public void init(PortInfo _info) 
	{
		String isUseSsl = _info.getAttribute("ssl");
		if(isUseSsl != null)
		{
			if(isUseSsl.equals("yes"))
			{
				try
				{
					SelfSignedCertificate ssc = new SelfSignedCertificate();
					sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
				}
				catch(Exception e)
				{
					this.logger.error("Failed to create https ssl context.", e);
				}
			}
		}
	}

	@Override
	public ChannelHandler[] make(SocketChannel _socketChannel) 
	{
		if(this.sslCtx != null)
		{
			return new ChannelHandler[]{this.sslCtx.newHandler(_socketChannel.alloc()), 
					new HttpRequestDecoder(), new HttpResponseEncoder()};
		}
		else
		{
			return new ChannelHandler[]{new HttpRequestDecoder(), new HttpResponseEncoder()};
		}
	}

}
