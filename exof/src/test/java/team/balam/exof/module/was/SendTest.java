package team.balam.exof.module.was;

import io.netty.handler.codec.http.*;
import org.junit.Assert;
import org.junit.Test;
import team.balam.exof.client.DefaultClient;
import team.balam.exof.client.component.HttpClientCodec;

import java.net.URI;

public class SendTest {
    @Test
    public void sendRequest() throws Exception {
        String[] testUri = new String[]{"/autoScan/rest", "/index.html"};

        for (String u : testUri) {
            URI uri = new URI(u);
            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
            request.headers().set(HttpHeaderNames.HOST, "localhost");
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

            team.balam.exof.client.Client sender = new DefaultClient(new HttpClientCodec());
            sender.setConnectTimeout(3000);

            sender.connect("localhost", 8080);
            FullHttpResponse response = sender.sendAndWait(request);
            sender.close();

            System.out.println(response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            Assert.assertEquals(200, response.status().code());
        }
    }
}
