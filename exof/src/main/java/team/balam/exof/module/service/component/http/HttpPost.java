package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpMethod;

@Deprecated
public class HttpPost extends HttpMethodFilter {
	public HttpPost() {
	    super(HttpMethod.POST);
    }
}
