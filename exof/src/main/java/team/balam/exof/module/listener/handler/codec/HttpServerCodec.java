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

import team.balam.exof.Constant;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class HttpServerCodec implements ChannelHandlerArray
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private int maxLength;
	private SelfSignedCertificate certificate;
	private SslContext sslCtx;
	
	@Override
	public void init(PortInfo _info) 
	{
		this.maxLength = _info.getAttributeToInt(EnvKey.Listener.MAX_LENGTH, 1024 * 8);
		
		String isUseSsl = _info.getAttribute(EnvKey.Listener.SSL, Constant.NO);
		if(Constant.YES.equals(isUseSsl))
		{
			try
			{
				String certPath = _info.getAttribute(EnvKey.Listener.CERTIFICATE_PATH);
				String priKeyPath = _info.getAttribute(EnvKey.Listener.PRIVATE_KEY_PATH);
				
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
	public ChannelHandler[] make(SocketChannel _socketChannel) {
		if (this.sslCtx != null) {
			return new ChannelHandler[]{this.sslCtx.newHandler(_socketChannel.alloc()),
					new io.netty.handler.codec.http.HttpServerCodec(4096, 8192, this.maxLength),
					new HttpObjectAggregator(this.maxLength)};
		} else {
			return new ChannelHandler[]{new io.netty.handler.codec.http.HttpServerCodec(4096, 8192, this.maxLength),
					new HttpObjectAggregator(this.maxLength)};
		}
	}
}
