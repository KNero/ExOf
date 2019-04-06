package team.balam.exof.module.service.component;

import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.http.ResponseToJson;

import javax.servlet.http.HttpServletRequest;

public class ResponseToJsonTest {
    @Test
    public void testServlet() throws Exception {
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(request, "getHeader", HttpHeaders.ACCEPT).thenReturn(HttpHeaderValues.APPLICATION_JSON.toString());

        ServiceObject serviceObject = new ServiceObject("");
        serviceObject.setRequest(request);

        RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

        Response res = new Response();
        res.a = "AA";
        res.b = "BB";

        ResponseToJson responseToJson = new ResponseToJson();
        String json = (String) responseToJson.execute(res);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

        Response res2 = mapper.readValue(json, Response.class);

        Assert.assertEquals(res.a, res2.a);
        Assert.assertEquals(res.b, res2.b);
    }

    @Test
    public void testServletNotConvert() throws Exception {
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(request, "getHeader", HttpHeaders.ACCEPT).thenReturn(HttpHeaderValues.TEXT_PLAIN.toString());

        ServiceObject serviceObject = new ServiceObject("");
        serviceObject.setRequest(request);

        RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

        Response res = new Response();
        res.a = "AA";
        res.b = "BB";

        ResponseToJson responseToJson = new ResponseToJson();
        Response json = (Response) responseToJson.execute(res);

        Assert.assertEquals(res, json); // http request 의 accept 가 application/json 이 아닐 경우 객체를 그대로 반환한다.
    }

    @Test
    public void testNetty() throws Exception {
        io.netty.handler.codec.http.HttpHeaders headers = PowerMockito.mock(io.netty.handler.codec.http.HttpHeaders.class);
        PowerMockito.when(headers, "get", HttpHeaderNames.ACCEPT).thenReturn(HttpHeaderValues.APPLICATION_JSON.toString());

        FullHttpRequest request = PowerMockito.mock(FullHttpRequest.class);
        PowerMockito.when(request, "headers").thenReturn(headers);

        ServiceObject serviceObject = new ServiceObject("");
        serviceObject.setRequest(request);

        RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

        Response res = new Response();
        res.a = "AA";
        res.b = "BB";

        ResponseToJson responseToJson = new ResponseToJson();
        String json = (String) responseToJson.execute(res);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

        Response res2 = mapper.readValue(json, Response.class);

        Assert.assertEquals(res.a, res2.a);
        Assert.assertEquals(res.b, res2.b);
    }

    @Test
    public void testNettyNotConvert() throws Exception {
        io.netty.handler.codec.http.HttpHeaders headers = PowerMockito.mock(io.netty.handler.codec.http.HttpHeaders.class);
        PowerMockito.when(headers, "get", HttpHeaderNames.ACCEPT).thenReturn(HttpHeaderValues.TEXT_PLAIN.toString());

        FullHttpRequest request = PowerMockito.mock(FullHttpRequest.class);
        PowerMockito.when(request, "headers").thenReturn(headers);

        ServiceObject serviceObject = new ServiceObject("");
        serviceObject.setRequest(request);

        RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

        Response res = new Response();
        res.a = "AA";
        res.b = "BB";

        ResponseToJson responseToJson = new ResponseToJson();
        Response json = (Response) responseToJson.execute(res);

        Assert.assertEquals(res, json); // http request 의 accept 가 application/json 이 아닐 경우 객체를 그대로 반환한다.
    }

    @Test
    public void test() throws Exception {
        ServiceObject serviceObject = new ServiceObject("");
        serviceObject.setRequest(new Object()); // 일반 객체일 경우 무조건 json 으로 변환

        RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

        Response res = new Response();
        res.a = "AA";
        res.b = "BB";

        ResponseToJson responseToJson = new ResponseToJson();
        String json = (String) responseToJson.execute(res);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

        Response res2 = mapper.readValue(json, Response.class);

        Assert.assertEquals(res.a, res2.a);
        Assert.assertEquals(res.b, res2.b);
    }

    static class Response {
        private String a;
        private String b;
    }
}