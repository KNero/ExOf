package team.balam.exof.module.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class HttpServerCodec extends ChannelHandlerArray
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SelfSignedCertificate certificate;
	private SslContext sslCtx;
	
	@Override
	public void init(PortInfo _info) 
	{
		String isUseSsl = _info.getAttribute("ssl", "no");
		if("yes".equals(isUseSsl))
		{
			try
			{
				String certPath = _info.getAttribute("certificatePath");
				String priKeyPath = _info.getAttribute("privateKeyPath");
				
				if((certPath == null || certPath.length() == 0) && (priKeyPath == null || priKeyPath.length() == 0))
				{
					this.certificate = new SelfSignedCertificate();
					this.sslCtx = SslContextBuilder.forServer(this.certificate.certificate(), this.certificate.privateKey()).build();
				}
				else
				{
					if(certPath == null)
					{
						this.logger.error("SSL ERROR ====> Certificate path is empty");
						return;
					}
					
					if(priKeyPath == null)
					{
						this.logger.error("SSL ERROR ====> Private key path is empty");
						return;
					}
					
					this.sslCtx = SslContextBuilder.forServer(new File(certPath), new File(priKeyPath)).build();
				}
			}
			catch(Exception e)
			{
				this.logger.error("Failed to create https ssl context.", e);
			}
		}
	}
	
	@Override
	public void destroy() 
	{
		if(this.certificate != null)
		{
			this.certificate.delete();
		}
	}

	@Override
	public ChannelHandler[] make(SocketChannel _socketChannel) 
	{
		if(this.sslCtx != null)
		{
			return new ChannelHandler[]{this.sslCtx.newHandler(_socketChannel.alloc()), 
					new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpObjectAggregator(this.maxLength)};
		}
		else
		{
			return new ChannelHandler[]{new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpObjectAggregator(this.maxLength)};
		}
	}

}
