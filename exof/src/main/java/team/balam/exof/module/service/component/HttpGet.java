package team.balam.exof.module.service.component;

import io.netty.handler.codec.http.HttpMethod;

public class HttpGet extends HttpMethodFilter {
	public HttpGet() {
	    super(HttpMethod.GET);
    }
}
