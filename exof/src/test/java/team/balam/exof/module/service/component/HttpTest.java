package team.balam.exof.module.service.component;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
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
import org.junit.Assert;
import org.junit.Test;
import team.balam.exof.TestInitializer;
import team.balam.exof.client.DefaultClient;

import java.net.URI;

public class HttpTest {
	@Test
	public void test_httpGet() throws Exception {
		URI uri = new URI("/test/http-get?paramA=pA&paramB=pB&name=권성민&list[]=권1&list[]=권2&list[]=권3&list[]=권4");
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
		request.headers().set(HttpHeaderNames.HOST, "localhost");
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

		team.balam.exof.client.Client sender = new DefaultClient(_socketChannel ->
				new ChannelHandler[]{new HttpClientCodec(), new HttpObjectAggregator(1048576)});

		sender.connect("localhost", 2001);
		FullHttpResponse response = sender.sendAndWait(request);
		if (response.status().code() != HttpResponseStatus.OK.code()) {
			Assert.fail("response code: " + response.status().code());
		}

		sender.close();
	}

	@Test
	public void test_httpPost() throws Exception {
		byte[] data = "{\"a\":\"aaaa\", \"b\":\"BBB\", \"number\":123, \"name\":\"권성민\"}".getBytes();

		URI uri = new URI("/test/http-post");
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath());
		request.headers().set(HttpHeaderNames.HOST, "localhost");
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
		request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		request.content().writeBytes(data);

		team.balam.exof.client.Client sender = new DefaultClient(_socketChannel ->
				new ChannelHandler[]{new HttpClientCodec(), new HttpObjectAggregator(1048576)});

		sender.connect("localhost", 2001);
		FullHttpResponse response = sender.sendAndWait(request);
		if (response.status().code() != HttpResponseStatus.OK.code()) {
			Assert.fail("response code: " + response.status().code());
		}

		sender.close();
	}
}
