package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpMethod;

@Deprecated
public class HttpPut extends HttpMethodFilter {
    public HttpPut() {
        super(HttpMethod.PUT);
    }
}
