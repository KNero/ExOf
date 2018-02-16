package team.balam.exof.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class ClientPool {
	private static final Logger LOG = LoggerFactory.getLogger(ClientPool.class);

	private static final String CHANNEL_HANDLER_MAKER = "channelHandlerMaker";

	private int readTimeout;
	private int maxPoolSize;
	private int acquireTimeout;

	private ChannelHandlerMaker channelHandlerMaker;
	private PoolHealthChecker poolHealthChecker;

	private AtomicLong indexCount = new AtomicLong();
	private InetSocketAddress[] target;
	private int targetSize;
	private ChannelPoolMap<InetSocketAddress, FixedChannelPool> pools;
	private EventLoopGroup eventLoopGroup;

	private void _init() {
		this.targetSize = this.target.length;
		this.eventLoopGroup = new NioEventLoopGroup();
		this.pools = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
			@Override
			protected FixedChannelPool newPool(InetSocketAddress key) {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);

				return new FixedChannelPool(bootstrap.remoteAddress(key), new AbstractChannelPoolHandler() {
					@Override
					public void channelCreated(Channel ch) {
					}
				}, ChannelHealthChecker.ACTIVE, FixedChannelPool.AcquireTimeoutAction.NEW,
						acquireTimeout, maxPoolSize, Integer.MAX_VALUE);
			}
		};

		this.poolHealthChecker.start();
	}

	public Client get() throws ClientPoolException {
		long currentIndex = this.indexCount.getAndIncrement();

		for (int i = 0; i < this.targetSize; ++i) {
			int index = (int) ((currentIndex + i) % this.targetSize);
			InetSocketAddress target = this.target[index];

			if (this.poolHealthChecker.isFail(target)) {
				continue;
			}

			FixedChannelPool targetPool = this.pools.get(target);
			Future<Channel> channelFuture = targetPool.acquire();

			try {
				Channel channel = channelFuture.get(this.acquireTimeout, TimeUnit.MILLISECONDS);
				Client client = new PooledClient(this._initChannelHandler(channel), targetPool, this.poolHealthChecker, target);
				client.setReadTimeout(this.readTimeout);

				return client;
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				this.poolHealthChecker.addFail(target);
				LOG.error("Can not get client from client pool", e);
			}
		}

		throw new ClientPoolException("All connection is wrong.");
	}

	private Channel _initChannelHandler(Channel _channel) {
		ChannelPipeline pipeline = _channel.pipeline();
		ChannelHandler[] channelHandlers = this.channelHandlerMaker.make((SocketChannel) _channel);

		for (int i = 0; i < channelHandlers.length; ++i) {
			String handlerName = CHANNEL_HANDLER_MAKER + i;
			if (pipeline.get(handlerName) != null) {
				pipeline.remove(handlerName);
			}
			pipeline.addLast(handlerName, channelHandlers[i]);
		}

		return _channel;
	}

	public void destroy() {
		this.poolHealthChecker.stop();
		this.eventLoopGroup.shutdownGracefully();
		for (InetSocketAddress address : this.target) {
			this.pools.get(address).close();
		}
	}

	public static class Builder {
		private List<InetSocketAddress> targetList = new ArrayList<>();
		private int readTimeout = DefaultClient.DEFAULT_READ_TIMEOUT;
		private int maxPoolSize = 10;
		private int acquireTimeout = 5000;
		private ChannelHandlerMaker channelHandlerMaker;
		private ClientHealthChecker clientHealthChecker;

		public Builder setReadTimeout(int readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public Builder addTarget(String _host, int _port) {
			this.targetList.add(new InetSocketAddress(_host, _port));
			return this;
		}

		public Builder setChannelHandlerMaker(ChannelHandlerMaker channelHandlerMaker) {
			this.channelHandlerMaker = channelHandlerMaker;
			return this;
		}

		public Builder setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
			return this;
		}

		public Builder setAcquireTimeout(int acquireTimeout) {
			this.acquireTimeout = acquireTimeout;
			return this;
		}

		public Builder setClientHealthChecker(ClientHealthChecker clientHealthChecker) {
			this.clientHealthChecker = clientHealthChecker;
			return this;
		}

		public ClientPool build() {
			ClientPool clientPool = new ClientPool();
			clientPool.target = this.targetList.toArray(new InetSocketAddress[this.targetList.size()]);
			clientPool.readTimeout = this.readTimeout;
			clientPool.maxPoolSize = this.maxPoolSize;
			clientPool.acquireTimeout = this.acquireTimeout;
			clientPool.channelHandlerMaker = this.channelHandlerMaker;

			if (clientPool.target.length == 0) {
				throw new IllegalArgumentException("Target host size is 0.");
			}

			if (clientPool.channelHandlerMaker == null) {
				throw new IllegalArgumentException("ChannelHandlerMaker is null");
			}

			if (this.clientHealthChecker == null) {
				this.clientHealthChecker = _client -> true;
			}

			clientPool.poolHealthChecker = new PoolHealthChecker(this.clientHealthChecker, clientPool.channelHandlerMaker);
			clientPool._init();
			return clientPool;
		}
	}
}
