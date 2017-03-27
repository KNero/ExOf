package team.balam.exof.module.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;
import team.balam.exof.module.listener.handler.RequestServiceHandler;
import team.balam.exof.module.listener.handler.SessionEventHandler;
import team.balam.exof.module.listener.handler.codec.LengthFieldByteCodec;
import team.balam.exof.module.listener.handler.transform.ServiceObjectTransform;

public class ServerPort
{
	private ServerPort self = this;
	
	private PortInfo portInfo;
	
	private Channel channel;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	private ChannelHandlerArray channelHandlerArray;
	
	public ServerPort(PortInfo _info)
	{
		this.portInfo = _info;
	}
	
	public int getNumber()
	{
		return this.portInfo.getAttributeToInt(EnvKey.Listener.NUMBER, 0);
	}
	
	public void open() throws Exception
	{
		RequestServiceHandler requestHandler = new RequestServiceHandler();
		
		if(this.portInfo.getChannelHandler() != null)
		{
			this.channelHandlerArray = 
					(ChannelHandlerArray)Class.forName(this.portInfo.getChannelHandler()).newInstance(); 
		}
		else
		{
			this.channelHandlerArray = new LengthFieldByteCodec();
		}
		
		this.channelHandlerArray.init(this.portInfo);
		
		if(this.portInfo.getMessageTransform() != null)
		{
			@SuppressWarnings("rawtypes")
			ServiceObjectTransform messageTransform = 
					(ServiceObjectTransform)Class.forName(this.portInfo.getMessageTransform()).newInstance();
			requestHandler.setServiceObjectTransform(messageTransform);
			
			messageTransform.init(this.portInfo);
		}
		
		if(this.portInfo.getSessionHandler() != null)
		{
			SessionEventHandler sessionHandler = 
					(SessionEventHandler)Class.forName(this.portInfo.getSessionHandler()).newInstance();
			requestHandler.setSessionEventHandler(sessionHandler);
			
			sessionHandler.init(this.portInfo);
		}
		
		this.bossGroup = new NioEventLoopGroup();
		
		int defaultWorkerSize = Runtime.getRuntime().availableProcessors() + 1;
		int workerSize = this.portInfo.getAttributeToInt(EnvKey.Listener.WORKER_SIZE, defaultWorkerSize);
		this.workerGroup = new NioEventLoopGroup(workerSize);
		
		ServerBootstrap b = new ServerBootstrap();
		b.group(this.bossGroup, this.workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel _socketChannel) throws Exception 
				{
					_socketChannel.pipeline().addLast(self.channelHandlerArray.make(_socketChannel)).addLast(requestHandler);
				}
			})
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.SO_REUSEADDR, true);
		
		int port = this.portInfo.getAttributeToInt(EnvKey.Listener.NUMBER, 0);
		ChannelFuture future = b.bind(port).sync(); 
		this.channel = future.channel();
	}
	
	public void close() throws Exception
	{
		this.channelHandlerArray.destroy();
		
		if(this.channel != null)
		{
			this.channel.close();
		}
		
		if(this.workerGroup != null)
		{
			this.workerGroup.shutdownGracefully();
		}
		
		if(this.bossGroup != null)
		{
			this.bossGroup.shutdownGracefully();
		}
	}
}
