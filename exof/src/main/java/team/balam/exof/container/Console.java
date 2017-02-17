package team.balam.exof.container;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import team.balam.exof.ConstantKey;
import team.balam.exof.Container;
import team.balam.exof.container.console.ConsoleCommandHandler;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;
import team.balam.exof.module.listener.handler.codec.NullDelimiterStringCodec;

public class Console implements Container
{
	private Channel channle;
	private EventLoopGroup workerGroup;
	
	private ChannelHandlerArray handlerArray;
	
	@Override
	public String getName()
	{
		return "Console";
	}

	@Override
	public void start() throws Exception
	{
		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT);
		portList.forEach(port -> {
			String isConsole = port.getAttribute(EnvKey.Listener.CONSOLE);
			if(ConstantKey.YES.equals(isConsole))
			{
				this.handlerArray = new NullDelimiterStringCodec();
				this.handlerArray.setMaxLength(1024 * 8);
				this.workerGroup = new NioEventLoopGroup();
				
				ServerBootstrap bootstrap = new ServerBootstrap();
				bootstrap.group(this.workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() 
					{
						protected void initChannel(SocketChannel ch) throws Exception 
						{
							ch.pipeline().addLast(handlerArray.make(ch)).addLast(new ConsoleCommandHandler());
						}
					});
				
				this.channle = bootstrap.bind(port.getNumber()).channel();
			}
		});
	}

	@Override
	public void stop() throws Exception
	{
		if(this.channle != null)
		{
			this.channle.close();
		}
		
		if(this.workerGroup != null)
		{
			this.workerGroup.shutdownGracefully();
		}
	}
}
