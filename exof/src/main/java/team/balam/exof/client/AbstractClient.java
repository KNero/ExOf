package team.balam.exof.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class AbstractClient<I, O> extends ChannelInboundHandlerAdapter implements Client<I, O>
{
	protected Channel channel;

	private EventLoopGroup workerGorup;
	private ChannelHandler[] channelHandler;
	
	private BlockingQueue<Object> responseQueue;
	private int connectTimeout;
	protected int readTimeout;
	
	public AbstractClient(ChannelHandler[] _channelHandler)
	{
		this.workerGorup = new NioEventLoopGroup();
		this.channelHandler = _channelHandler;
		this.connectTimeout = Client.DEFAULT_CONNECT_TIMEOUT;
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
		b.group(this.workerGorup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>(){
			protected void initChannel(SocketChannel _channel) throws Exception 
			{
				_channel.pipeline().addLast(channelHandler).addLast(this);
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
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		this.responseQueue.add(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		this.responseQueue.add(cause);
	}
	
	protected ResponseFuture<O> makeResponse()
	{
		return new ResponseFutureImpl<O>(this.responseQueue);
	}
	
	@Override
	public void close() throws IOException 
	{
		if(this.channel != null)
		{
			this.channel.close();
		}
		
		this.workerGorup.shutdownGracefully();
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
