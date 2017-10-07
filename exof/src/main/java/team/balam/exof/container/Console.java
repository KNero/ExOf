package team.balam.exof.container;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.LoggerFactory;
import team.balam.exof.Container;
import team.balam.exof.container.console.ConsoleCommandHandler;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;
import team.balam.exof.module.listener.handler.codec.NullDelimiterStringCodec;
import team.balam.exof.module.was.JettyModule;

public class Console implements Container {
	private Channel channel;
	private EventLoopGroup workerGroup;
	private ChannelHandlerArray handlerArray;
	
	private JettyModule webConsole;

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public void start() throws Exception {
		PortInfo consolePort = ListenerDao.selectSpecialPort(EnvKey.Listener.CONSOLE);
		if (!consolePort.isNull()) {
			this.openConsolePort(consolePort);
		}

		PortInfo adminPort = ListenerDao.selectSpecialPort(EnvKey.Listener.ADMIN_CONSOLE);
		if (!adminPort.isNull()) {
			this.createWebConsole(adminPort);
		}
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
		this.channel = future.channel();

		LoggerFactory.getLogger(this.getClass()).info("Console Monitoring Port : {}", port);
	}
	
	private void createWebConsole(PortInfo port) throws Exception {
		ListenerDao.insertPortAttribute(port.getNumber(), EnvKey.Listener.HTTP, String.valueOf(port.getNumber()));
		ListenerDao.insertPortAttribute(port.getNumber(), EnvKey.Listener.DESCRIPTOR, "./admin_console/WEB-INF/web.xml");
		ListenerDao.insertPortAttribute(port.getNumber(), EnvKey.Listener.RESOURCE_BASE, "./admin_console");
		ListenerDao.insertPortAttribute(port.getNumber(), EnvKey.Listener.CONTEXT_PATH, "/");
		
		this.webConsole = new JettyModule();
		this.webConsole.setPortInfo(port);
		this.webConsole.start();

		LoggerFactory.getLogger(this.getClass()).info("Admin console Port : {}", port);
	}

	@Override
	public void stop() throws Exception {
		if (this.webConsole != null) {
			this.webConsole.stop();
		}

		if (this.channel != null) {
			this.channel.close();
		}

		if (this.workerGroup != null) {
			this.workerGroup.shutdownGracefully();
		}
	}
}
