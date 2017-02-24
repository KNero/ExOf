package team.balam.exof.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractClient implements Client
{
	private ChannelFuture channelFuture;

	private EventLoopGroup workerGorup;
	private ChannelHandler[] channelHandler;
	
	private int connectTimeout;
	protected int readTimeout;
	
	public AbstractClient(ChannelHandler[] _channelHandler)
	{
		this.workerGorup = new NioEventLoopGroup();
		this.channelHandler = _channelHandler;
		this.connectTimeout = Client.DEFAULT_CONNECT_TIMEOUT;
	}
	
	@Override
	public void setConnectTimeout(int _time) 
	{
		this.connectTimeout = _time;
	}
	
	@Override
	public void setReadTimeout(int _time) 
	{
		this.readTimeout = _time;
	}
	
	@Override
	public void connect(String _host, int _port) throws IOException 
	{
		Bootstrap b = new Bootstrap();
		b.group(this.workerGorup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>(){
			protected void initChannel(SocketChannel _channel) throws Exception 
			{
				_channel.pipeline().addLast(channelHandler);
			}
		});
		
		this.channelFuture = b.connect(_host, _port);

		try 
		{
			this.channelFuture.get(this.connectTimeout, TimeUnit.MILLISECONDS);
		} 
		catch(InterruptedException | ExecutionException | TimeoutException e) 
		{
			throw new IOException("Can't connect to remote ip[" + _host + "] port[" + _port + "]", e);
		}
	}
	
	@Override
	public void close() throws IOException 
	{
		if(this.channelFuture != null)
		{
			this.channelFuture.channel().close();
		}
		
		this.workerGorup.shutdownGracefully();
	}
	
	@Override
	public String toString() 
	{
		if(this.channelFuture != null && this.channelFuture.channel() != null)
		{
			return this.channelFuture.channel().toString();
		}
		else
		{
			return super.toString();
		}
	}
}
