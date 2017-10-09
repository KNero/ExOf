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
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.client.ResponseFuture;
import team.balam.exof.client.Sender;

import java.io.File;
import java.io.IOException;

public class DeployRequestor {
	private Logger logger = LoggerFactory.getLogger(DeployRequestor.class);

	private String host;
	private int port;
	private Sender<HttpRequest, FullHttpResponse> sender;

	public DeployRequestor(String _host, int _port) {
		this.host = _host;
		this.port = _port;
		this.sender = new Sender<>(_socketChannel -> new ChannelHandler[]{
				new HttpClientCodec(), new ChunkedWriteHandler(), new HttpObjectAggregator(1048576)
		});
	}

	public void sendExternalLib(File file) throws FailedDeployException {
		HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/exof/deploy/library");
		request.headers().set(HttpHeaderNames.HOST, this.host);
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

		try {
			HttpPostRequestEncoder requestEncoder = new HttpPostRequestEncoder(request, true);
			requestEncoder.addBodyFileUpload("library", file, "application/octet-stream", false);

			this.sender.connect(this.host, this.port);
			this.sender.send(requestEncoder.finalizeRequest());
			this.sender.send(requestEncoder);

			ResponseFuture<FullHttpResponse> responseFuture = this.sender.getResponse();
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

//		HttpDataFactory httpDataFactory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
//		httpDataFactory.createFileUpload(request, file.getName(), file.getName(),
//				"application/octet-stream", "utf-8", CharsetUtil.UTF_8, file.length());
//
//		HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(httpDataFactory, request);

	}

	public void reloadService() throws FailedDeployException {
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/exof/deploy/service");
		request.headers().set(HttpHeaderNames.HOST, this.host);
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

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

	private void _close() {
		try {
			this.sender.close();
		} catch (IOException e) {
			this.logger.error("Can't close connection.", e);
		}
	}
}
