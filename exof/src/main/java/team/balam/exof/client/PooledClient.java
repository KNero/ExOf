package team.balam.exof.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.FixedChannelPool;

import java.net.InetSocketAddress;

public class PooledClient implements Client {
	private static final String RESPONSE_HANDLER_NAME= "responseHandler";

	private boolean isClosed;
	private FixedChannelPool pool;
	private Channel channel;
	private ResponseFutureImpl responseFuture;
	private int readTimeout;

	private PoolHealthChecker poolHealthChecker;
	private InetSocketAddress target;

	PooledClient(Channel _channel, FixedChannelPool _pool, PoolHealthChecker _poolHealthChecker, InetSocketAddress _target) {
		this.pool = _pool;
		this.responseFuture = new ResponseFutureImpl();
		this.channel = _channel;
		this.poolHealthChecker = _poolHealthChecker;
		this.target = _target;

		ChannelPipeline pipeline = this.channel.pipeline();
		if (pipeline.get(RESPONSE_HANDLER_NAME) != null) {
			pipeline.remove(RESPONSE_HANDLER_NAME);
		}
		pipeline.addLast(RESPONSE_HANDLER_NAME, this.responseFuture);
	}

	@Override
	public void connect(String _host, int _port) {
		throw new UnsupportedOperationException("PooledClient is unsupported.");
	}

	@Override
	public void setReadTimeout(int _timeout) {
		this.readTimeout = _timeout;
	}

	@Override
	public void setConnectTimeout(int _timeout) {

	}

	@Override
	public void send(Object _data) {
		this.channel.write(_data);
	}

	@Override
	public void flush() {
		this.channel.flush();
	}

	@Override
	public ResponseFuture getResponse() {
		return this.responseFuture;
	}

	@Override
	public <T> T sendAndWait(Object _data) throws Exception {
		this.channel.write(_data);
		this.channel.flush();

		this.responseFuture.await(this.readTimeout);
		try {
			return this.responseFuture.get();
		} catch (Exception e) {
			this.poolHealthChecker.addFail(this.target);
			throw e;
		}
	}

	@Override
	public void close() {
		if (!this.isClosed) {
			this.pool.release(this.channel);
			this.isClosed = true;
		}
	}
}
