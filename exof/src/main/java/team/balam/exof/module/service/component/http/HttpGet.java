package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpMethod;

@Deprecated
public class HttpGet extends HttpMethodFilter {
	public HttpGet() {
	    super(HttpMethod.GET);
    }
}
