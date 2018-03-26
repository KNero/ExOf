package team.balam.exof.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PoolHealthChecker implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(PoolHealthChecker.class);

	private Set<InetSocketAddress> failTarget = new CopyOnWriteArraySet<>();
	private ClientHealthChecker healthChecker;
	private ChannelHandlerMaker channelHandlerMaker;
	private volatile boolean isRunning;

	PoolHealthChecker(ClientHealthChecker _healthChecker, ChannelHandlerMaker _handlerMaker) {
		this.healthChecker = _healthChecker;
		this.channelHandlerMaker = _handlerMaker;
	}

	void start() {
		this.isRunning = true;
		new Thread(this).start();
	}

	void stop() {
		this.isRunning = false;
	}

	void addFail(InetSocketAddress _target) {
		this.failTarget.add(_target);
	}

	boolean isFail(InetSocketAddress _target) {
		return !this.failTarget.isEmpty() && this.failTarget.contains(_target);
	}

	@Override
	public final void run() {
		while (this.isRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//ignore
			}

			if (failTarget.isEmpty()) {
				continue;
			}

			for (InetSocketAddress target : this.failTarget) {
				try (Client client = new DefaultClient(this.channelHandlerMaker)) {
					client.connect(target.getHostName(), target.getPort());

					if (this.healthChecker.check( client)) {
						this.failTarget.remove(target);
						LOG.info("[{}] pool's checking health is success.", target.toString());
					} else {
						throw new Exception ("Target state is not normal.");
					}
				} catch (Exception e) {
					LOG.error("Fail to close client.", e);
					continue;
				}

				this.failTarget.remove(target);
			}
		}
	}
}
