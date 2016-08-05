package team.balam.exof.container.console.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import team.balam.exof.ConstantKey;
import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.CommandBuilder;

public class Client
{
	public static void main(String[] _arge) throws Exception
	{
		new Viewer().start();
	}
	
	public static void Send(Command _command, java.util.function.Consumer<Map<String, Object>> _callback)
	{
		EventLoopGroup group = null;
		
		try
		{
			group = new NioEventLoopGroup(1);
			
			Bootstrap boot = new Bootstrap();
			boot.group(group);
			boot.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8000, Delimiters.nulDelimiter()))
						.addLast(new StringEncoder(Charset.forName(ConstantKey.NETWORK_CHARSET)))
						.addLast(new StringDecoder(Charset.forName(ConstantKey.NETWORK_CHARSET)))
						.addLast(new SimpleChannelInboundHandler<String>() 
						{
							@Override
							protected void channelRead0(ChannelHandlerContext _ctx, String _msg) throws Exception
							{
								try
								{
									ObjectMapper objectMapper = new ObjectMapper();
									TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>(){};
									
									_callback.accept(objectMapper.readValue(_msg, mapType));
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								
								_ctx.close();
							}
						});
				}
				
			});
			
			Channel channel = boot.connect("localhost", 3333).sync().channel();
			
			Command cmd = CommandBuilder.buildServiceListGetter();
			channel.writeAndFlush(cmd.toJson());
			
			channel.closeFuture().sync();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			group.shutdownGracefully();
		}
	}
}
