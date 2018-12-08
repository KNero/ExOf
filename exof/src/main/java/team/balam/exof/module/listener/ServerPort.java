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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;
import team.balam.exof.module.listener.handler.ChannelInitializerException;
import team.balam.exof.module.listener.handler.RequestServiceHandler;
import team.balam.exof.module.listener.handler.SessionEventHandler;
import team.balam.exof.module.listener.handler.transform.ServiceObjectTransform;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerPort extends ChannelInitializer<SocketChannel> {
	private PortInfo portInfo;
	
	private Channel channel;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	private ChannelHandlerArray channelHandlerArray;
	private RequestServiceHandler requestHandler;
	
	ServerPort(PortInfo _info)
	{
		this.portInfo = _info;
	}
	
	public int getNumber()
	{
		return this.portInfo.getNumber();
	}
	
	public void open() throws Exception {
	    initChannelHandler();
	    initRequestServiceHandler();

		this.bossGroup = new NioEventLoopGroup();
		
		int defaultWorkerSize = Runtime.getRuntime().availableProcessors() + 1;
		int workerSize = this.portInfo.getAttributeToInt(EnvKey.Listener.WORKER_SIZE, defaultWorkerSize);
		Executor workerExecutor = new ThreadPoolExecutor(workerSize, workerSize, 1, TimeUnit.SECONDS, new SynchronousQueue<>());
		
		this.workerGroup = new NioEventLoopGroup(workerSize, workerExecutor);

		ServerBootstrap b = new ServerBootstrap();
		b.group(this.bossGroup, this.workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(this)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.SO_REUSEADDR, true);
		
		int port = this.portInfo.getNumber();
		ChannelFuture future = b.bind(port).sync(); 
		this.channel = future.channel();
	}

	private void initChannelHandler() throws Exception {
        String channelHandler = this.portInfo.getChannelHandler();
        if (!channelHandler.isEmpty()) {
            Object handlerArray = ExternalClassLoader.loadClass(channelHandler).newInstance();
            if (handlerArray instanceof ChannelHandlerArray) {
                this.channelHandlerArray = (ChannelHandlerArray) handlerArray;
                this.channelHandlerArray.init(this.portInfo);
            } else {
                throw new ServerPortInitializeException("channelHandler is [instance of ChannelHandlerArray]");
            }
        } else {
            throw new ServerPortInitializeException("channelHandler is null. Check listener.xml");
        }
    }

	private void initRequestServiceHandler() throws Exception {
		requestHandler = new RequestServiceHandler();

		String messageTransformClass = this.portInfo.getMessageTransform();
		if (!messageTransformClass.isEmpty()) {

		    Object transform = ExternalClassLoader.loadClass(messageTransformClass).newInstance();
		    if (transform instanceof ServiceObjectTransform) {
                ServiceObjectTransform messageTransform = (ServiceObjectTransform) transform;
                messageTransform.init(this.portInfo);

                requestHandler.setServiceObjectTransform(messageTransform);
            } else {
                throw new ServerPortInitializeException("messageTransform is [instance of ServiceObjectTransform]");
            }
		} else {
			throw new ServerPortInitializeException("messageTransform is null. Check listener.xml");
		}

		String sessionHandlerClass = this.portInfo.getSessionHandler();
		if (!sessionHandlerClass.isEmpty()) {

		    Object handler = ExternalClassLoader.loadClass(sessionHandlerClass).newInstance();
		    if (handler instanceof SessionEventHandler) {
                SessionEventHandler sessionHandler = (SessionEventHandler) handler;
                requestHandler.setSessionEventHandler(sessionHandler);

                sessionHandler.init(this.portInfo);
            } else {
                throw new ServerPortInitializeException("sessionHandler is [instance of SessionEventHandler]");
            }
		}
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		try {
			socketChannel.pipeline().addLast(this.channelHandlerArray.make(socketChannel)).addLast(requestHandler);
		} catch (ChannelInitializerException e) {
			LoggerFactory.getLogger(ServerPort.class).error("Fail to create channel pipeline", e);
			throw e;
		}
	}

	public void close() {
	    if (this.channelHandlerArray != null) {
            this.channelHandlerArray.destroy();
        }

		if (this.channel != null) {
			this.channel.close();
		}

		if (this.workerGroup != null) {
			this.workerGroup.shutdownGracefully();
		}

		if (this.bossGroup != null) {
			this.bossGroup.shutdownGracefully();
		}
	}
}
