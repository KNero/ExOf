package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpMethod;

public class HttpPatch extends HttpMethodFilter {
	public HttpPatch() {
		super(HttpMethod.PATCH);
	}
}
