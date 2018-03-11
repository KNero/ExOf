package team.balam.exof.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.ServerPort;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;
import team.balam.exof.module.listener.handler.ChannelInitializerException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultClient implements Client
{
	private static final int DEFAULT_CONNECT_TIMEOUT = 3000;
	static final int DEFAULT_READ_TIMEOUT = 10000;

	protected Channel channel;

	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	private ChannelHandlerMaker channelHandler;
	
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = DEFAULT_READ_TIMEOUT;
	
	private ResponseFutureImpl response;

	public DefaultClient(ChannelHandlerMaker _channelHandler) {
		this.channelHandler = _channelHandler;
	}
	
	@Override
	public void setConnectTimeout(int _timeout) 
	{
		this.connectTimeout = _timeout;
	}
	
	@Override
	public void setReadTimeout(int _timeout) 
	{
		this.readTimeout = _timeout;
	}
	
	@Override
	public void connect(String _host, int _port) throws IOException 
	{
		Bootstrap b = new Bootstrap();
		b.group(this.workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>(){
			protected void initChannel(SocketChannel _channel)
			{
				try {
					response = new ResponseFutureImpl();
					_channel.pipeline().addLast(channelHandler.make(_channel)).addLast(response);
				} catch (ChannelInitializerException e) {
					LoggerFactory.getLogger(DefaultClient.class).error("Fail to create channel pipeline", e);
				}
			}
		});

		ChannelFuture channelFuture = b.connect(_host, _port);

		try 
		{
			channelFuture.get(this.connectTimeout, TimeUnit.MILLISECONDS);
			this.channel = channelFuture.channel();
		} 
		catch(InterruptedException | ExecutionException | TimeoutException e) 
		{
			throw new IOException("Can't connect to remote ip[" + _host + "] port[" + _port + "]", e);
		}
	}

	@Override
	public void flush() {
		this.channel.flush();
	}

	@Override
	public void send(Object _data) {
		this.channel.write(_data);
	}

	@Override
	public <T> T sendAndWait(Object _data) throws Exception {
		this.channel.write(_data);
		this.channel.flush();

		this.response.await(this.readTimeout);
		return this.response.get();
	}

	public ResponseFuture getResponse() {
		return this.response;
	}
	
	@Override
	public void close() {
		if(this.channel != null) {
			this.channel.close();
		}
		this.workerGroup.shutdownGracefully();
	}
	
	@Override
	public String toString() 
	{
		if(this.channel != null)
		{
			return this.channel.toString();
		}
		else
		{
			return super.toString();
		}
	}
}
