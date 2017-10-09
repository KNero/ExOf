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
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultClient implements Client
{
	protected static final int DEFAULT_CONNECT_TIMEOUT = 3000;
	protected static final int DEFAULT_READ_TIMEOUT = 10000;

	protected Channel channel;

	private EventLoopGroup workerGroup;
	private ChannelHandlerMaker channelHandler;
	
	private int connectTimeout;
	protected int readTimeout;
	
	private ResponseFutureImpl response;
	
	public DefaultClient(ChannelHandlerMaker _channelHandler)
	{
		this(_channelHandler, new NioEventLoopGroup());
	}
	
	public DefaultClient(ChannelHandlerMaker _channelHandler, NioEventLoopGroup _loopGroup)
	{
		this.channelHandler = _channelHandler;
		this.workerGroup = _loopGroup;
		this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		this.readTimeout = DEFAULT_READ_TIMEOUT;
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
			protected void initChannel(SocketChannel _channel) throws Exception 
			{
				response = new ResponseFutureImpl();
				_channel.pipeline().addLast(channelHandler.make(_channel)).addLast(response);
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
	public void send(Object _data) throws Exception {
		this.channel.write(_data);
	}

	@Override
	public <T> T sendAndWait(Object _data) throws Exception {
		this.channel.write(_data);
		this.channel.flush();

		ResponseFuture resFuture = this.getResponse();
		resFuture.await(this.readTimeout);

		return resFuture.get();
	}

	public ResponseFuture getResponse() {
		return this.response;
	}
	
	@Override
	public void close() throws IOException 
	{
		if(this.channel != null)
		{
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
