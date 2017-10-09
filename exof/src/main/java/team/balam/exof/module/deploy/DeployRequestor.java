package team.balam.exof.module.deploy;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.client.Sender;

import java.io.IOException;

public class DeployRequestor {
	private Logger logger = LoggerFactory.getLogger(DeployRequestor.class);

	private String host;
	private int port;
	private Sender<FullHttpRequest, FullHttpResponse> sender;

	public DeployRequestor(String _host, int _port) {
		this.host = _host;
		this.port = _port;
		this.sender = new Sender<>(_socketChannel -> new ChannelHandler[]{
				new HttpClientCodec(), new HttpObjectAggregator(1048576)
		});
	}

	public void reloadService() throws FailedDeployException {
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/exof/deploy/service");
		request.headers().set(HttpHeaderNames.HOST, this.host);
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

		try {
			this.sender.connect(this.host, this.port);
			FullHttpResponse response = sender.sendAndWait(request);
			if (response.status().code() != HttpResponseStatus.OK.code()) {
				throw new FailedDeployException("Fail to deploy. Response code : " + response.status().code());
			}
		} catch (FailedDeployException e) {
			throw e;
		} catch (Exception e) {
			throw new FailedDeployException(e);
		} finally {
			this._close();
		}
	}

	private void _close() {
		try {
			this.sender.close();
		} catch (IOException e) {
			this.logger.error("Can't close connection.", e);
		}
	}
}
