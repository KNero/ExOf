package team.balam.exof.container;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import team.balam.exof.Constant;
import team.balam.exof.Container;
import team.balam.exof.container.console.ConsoleCommandHandler;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;
import team.balam.exof.module.listener.handler.codec.NullDelimiterStringCodec;
import team.balam.exof.module.was.JettyModule;

public class Console implements Container {
	private Channel channle;
	private EventLoopGroup workerGroup;
	private ChannelHandlerArray handlerArray;
	
	private JettyModule webConsole;

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public void start() throws Exception {
		List<PortInfo> deleteList = new LinkedList<>();

		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.FileName.LISTENER, EnvKey.Listener.PORT);
		for (PortInfo info : portList) {
			String isConsole = info.getAttribute(EnvKey.Listener.CONSOLE);
			if (Constant.YES.equals(isConsole)) {
				this.openConsolePort(info);
				
				deleteList.add(info);
			}
			
			String isWebConsole = info.getAttribute(EnvKey.Listener.WEB_CONSOLE);
			if (Constant.YES.equals(isWebConsole)) {
				this.createWebConsole(info);
				
				deleteList.add(info);
			}
		}

		portList.removeAll(deleteList);
	}
	
	private void openConsolePort(PortInfo consolePort) throws InterruptedException {
		this.handlerArray = new NullDelimiterStringCodec();

		try {
			this.handlerArray.init(consolePort);
		} catch (Exception e) {
			// 이 부분은 에러가 나지 않기 때문에 무시한다.
		}

		this.workerGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(this.workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(handlerArray.make(ch)).addLast(new ConsoleCommandHandler());
					}
				});

		int port = consolePort.getAttributeToInt(EnvKey.Listener.NUMBER, 0);
		ChannelFuture future = bootstrap.bind(port).sync();
		this.channle = future.channel();

		LoggerFactory.getLogger(this.getClass()).info("Console Monitoring Port : " + port);
	}
	
	private void createWebConsole(PortInfo port) throws Exception {
		port.addAttribute(EnvKey.Listener.HTTP, port.getAttribute(EnvKey.Listener.NUMBER));
		port.addAttribute(EnvKey.Listener.DESCRIPTOR, "./webconsole/WEB-INF/web.xml");
		port.addAttribute(EnvKey.Listener.RESOURCE_BASE, "./webconsole");
		port.addAttribute(EnvKey.Listener.CONTEXT_PATH, "/");
		
		this.webConsole = new JettyModule();
		this.webConsole.setPortInfo(port);
		this.webConsole.start();
		
		SystemSetting.getInstance().set(EnvKey.FileName.LISTENER, EnvKey.Listener.WEB_CONSOLE, port);
		LoggerFactory.getLogger(this.getClass()).info("WebConsole Port : " + port);
	}

	@Override
	public void stop() throws Exception {
		if (this.channle != null) {
			this.channle.close();
		}

		if (this.workerGroup != null) {
			this.workerGroup.shutdownGracefully();
		}
	}
}
