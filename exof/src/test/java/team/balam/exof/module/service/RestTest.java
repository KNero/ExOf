package team.balam.exof.module.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import team.balam.exof.TestInitializer;
import team.balam.exof.environment.ServiceLoader;
import team.balam.exof.test.scan.ScanTestService;

@FixMethodOrder
public class RestTest {
    @BeforeClass
    public static void init() throws Exception {
        TestInitializer.init();
        new ServiceLoader().load("./env");
        ServiceProvider.getInstance().start();
    }

    @Test
    public void callRestPathVariable() throws Exception {
        HttpRequest request = PowerMockito.mock(HttpRequest.class);
        PowerMockito.when(request.method()).thenReturn(HttpMethod.GET);

        ServiceObject so = new ServiceObject("/autoScan/rest/12345/test234");
        so.setRequest(request);
        so.setServiceGroupId("GET");

        ServiceWrapper service = ServiceProvider.lookup(so);
        service.call(so);
        ScanTestService test = service.getHost();
        Assert.assertEquals("12345", test.variable1);
        Assert.assertEquals("test234", test.variable2);
    }

    @Test
    public void callRestGetParameter() throws Exception {
        HttpRequest request = PowerMockito.mock(HttpRequest.class);
        PowerMockito.when(request.method()).thenReturn(HttpMethod.GET);
        PowerMockito.when(request.uri()).thenReturn("/autoScan/rest/get2?a=123&b=456");

        ServiceObject so = new ServiceObject("/autoScan/rest/get2");
        so.setRequest(request);
        so.setServiceGroupId("GET");

        ServiceWrapper service = ServiceProvider.lookup(so);
        service.call(so);
        ScanTestService test = service.getHost();
        Assert.assertEquals("123", test.variable1);
        Assert.assertEquals("456", test.variable2);
    }

    @Test
    public void callRestPostJsonBody() throws Exception {
        String data = "{\"a\": \"ksm\", \"b\": \"good\"}";
        ByteBuf body = Unpooled.copiedBuffer(data.getBytes());

        HttpHeaders httpHeaders = PowerMockito.mock(HttpHeaders.class);
        PowerMockito.when(httpHeaders.get(HttpHeaderNames.CONTENT_TYPE)).thenReturn(HttpHeaderValues.APPLICATION_JSON.toString());
        PowerMockito.when(httpHeaders.getInt(HttpHeaderNames.CONTENT_LENGTH)).thenReturn(data.getBytes().length);

        FullHttpRequest request = PowerMockito.mock(FullHttpRequest.class);
        PowerMockito.when(request.method()).thenReturn(HttpMethod.POST);
        PowerMockito.when(request.headers()).thenReturn(httpHeaders);
        PowerMockito.when(request.content()).thenReturn(body);
        PowerMockito.when(request.uri()).thenReturn("/autoScan/rest/post1/gogogo?a=3232");

        ServiceObject so = new ServiceObject("/autoScan/rest/post1/gogogo");
        so.setRequest(request);
        so.setServiceGroupId("POST");

        ServiceWrapper service = ServiceProvider.lookup(so);
        service.call(so);
        ScanTestService test = service.getHost();
        Assert.assertEquals("gogogo", test.variable1);
        Assert.assertEquals("3232", test.variable2);
        Assert.assertEquals("ksm", test.variable3);
        Assert.assertEquals("good", test.variable4);
    }

    @Test
    public void callRestPostStringQueryBody() throws Exception {
        String data = "?a=ksm2&b=good2&c=6";
        ByteBuf body = Unpooled.copiedBuffer(data.getBytes());

        HttpHeaders httpHeaders = PowerMockito.mock(HttpHeaders.class);
        PowerMockito.when(httpHeaders.getInt(HttpHeaderNames.CONTENT_LENGTH)).thenReturn(data.getBytes().length);

        FullHttpRequest request = PowerMockito.mock(FullHttpRequest.class);
        PowerMockito.when(request.method()).thenReturn(HttpMethod.POST);
        PowerMockito.when(request.headers()).thenReturn(httpHeaders);
        PowerMockito.when(request.content()).thenReturn(body);
        PowerMockito.when(request.uri()).thenReturn("/autoScan/rest/post1/gogogo?a=000");

        ServiceObject so = new ServiceObject("/autoScan/rest/post1/g1");
        so.setRequest(request);
        so.setServiceGroupId("POST");

        ServiceWrapper service = ServiceProvider.lookup(so);
        service.call(so);
        ScanTestService test = service.getHost();
        Assert.assertEquals("g1", test.variable1);
        Assert.assertEquals("000", test.variable2);
        Assert.assertEquals("ksm2", test.variable3);
        Assert.assertEquals("good2", test.variable4);
        Assert.assertEquals(6, test.variable5);
    }
}
