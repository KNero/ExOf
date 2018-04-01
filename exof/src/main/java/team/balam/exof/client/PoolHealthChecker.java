package team.balam.exof.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PoolHealthChecker implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(PoolHealthChecker.class);

	private static final int PING_INTERVAL = 10000;
	private Map<InetSocketAddress, Long> lastPingTime = new HashMap<>();

	private Set<InetSocketAddress> failTarget = new CopyOnWriteArraySet<>();
	private ClientHealthChecker healthChecker;
	private ChannelHandlerMaker channelHandlerMaker;
	private volatile boolean isRunning;

	PoolHealthChecker(ClientHealthChecker healthChecker, ChannelHandlerMaker handlerMaker) {
		this.healthChecker = healthChecker;
		this.channelHandlerMaker = handlerMaker;
	}

	void start() {
		this.isRunning = true;
		new Thread(this).start();
	}

	void stop() {
		this.isRunning = false;
	}

	void addFail(InetSocketAddress target) {
		this.failTarget.add(target);
	}

	void updateLastPingTime(InetSocketAddress target) {
		this.lastPingTime.put(target, System.currentTimeMillis());
	}

	boolean isFail(InetSocketAddress target) {
		return !this.failTarget.isEmpty() && this.failTarget.contains(target);
	}

	@Override
	public final void run() {
		while (this.isRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//ignore
			}

			if (!this.failTarget.isEmpty()) {
				for (InetSocketAddress target : this.failTarget) {
					try {
						this.send(target);
					} catch (Exception e) {
						LOG.error("Fail to close client.", e);
						continue;
					}

					this.failTarget.remove(target);
				}
			}

			for (Map.Entry<InetSocketAddress, Long> data : this.lastPingTime.entrySet()) {
				if (System.currentTimeMillis() - data.getValue() > PING_INTERVAL) {
					try {
						this.send(data.getKey());
					} catch (Exception e) {
						LOG.error("Fail to check ping.", e);
						this.failTarget.add(data.getKey());
					}
				}
			}
		}
	}

	private void send(InetSocketAddress target) throws Exception {
		try (Client client = new DefaultClient(this.channelHandlerMaker)) {
			client.connect(target.getHostName(), target.getPort());

			if (this.healthChecker.check( client)) {
				this.failTarget.remove(target);
				LOG.info("[{}] pool's checking health is success.", target);
			} else {
				throw new Exception ("Target state is not normal.");
			}
		}
	}
}
