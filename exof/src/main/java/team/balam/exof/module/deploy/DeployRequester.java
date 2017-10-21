package team.balam.exof.module.deploy;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.client.Client;
import team.balam.exof.client.DefaultClient;
import team.balam.exof.client.ResponseFuture;
import team.balam.exof.environment.TextCrypto;

import java.io.File;
import java.io.IOException;

public class DeployRequester {
	private Logger logger = LoggerFactory.getLogger(DeployRequester.class);

	public static final String DEPLOY_ID = "deploy_id";
	public static final String DEPLOY_PASSWORD = "deploy_password";

	private String host;
	private int port;
	private String id;
	private String password;
	private Client sender;

	public DeployRequester(String _host, int _port) {
		this.host = _host;
		this.port = _port;
		this.sender = new DefaultClient(_socketChannel -> new ChannelHandler[]{
				new HttpClientCodec(), new ChunkedWriteHandler(), new HttpObjectAggregator(1048576)
		});
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void sendExternalLib(File file) throws FailedDeployException {
		HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/exof/deploy/library");
		this._makeHeader(request);

		try {
			HttpPostRequestEncoder requestEncoder = new HttpPostRequestEncoder(request, true);
			requestEncoder.addBodyFileUpload("library", file, "application/octet-stream", false);

			this.sender.connect(this.host, this.port);
			this.sender.send(requestEncoder.finalizeRequest());
			this.sender.send(requestEncoder);
			this.sender.flush();

			ResponseFuture responseFuture = this.sender.getResponse();
			responseFuture.await(10000);
			FullHttpResponse response = responseFuture.get();

			if (response.status().code() != HttpResponseStatus.OK.code()) {
				throw new FailedDeployException("Fail to send library. Response code : " + response.status().code());
			}
		} catch (Exception e) {
			throw new FailedDeployException(e);
		} finally {
			this._close();
		}
	}

	public void reloadService() throws FailedDeployException {
		HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/exof/deploy/service");
		this._makeHeader(request);

		try {
			this.sender.connect(this.host, this.port);
			FullHttpResponse response = this.sender.sendAndWait(request);
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

	private void _makeHeader(HttpRequest request) throws FailedDeployException {
		request.headers().set(HttpHeaderNames.HOST, this.host);
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

		if (this.id != null && this.password != null) {
			request.headers().set(DEPLOY_ID, this.id);

			try {
				String encPassword = new TextCrypto().encodeBase64(this.password.getBytes());
				request.headers().set(DEPLOY_PASSWORD, encPassword);
			} catch (Exception e) {
				throw new FailedDeployException("Fail to encode password.", e);
			}
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
