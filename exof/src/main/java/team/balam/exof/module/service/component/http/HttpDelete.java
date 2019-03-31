package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpMethod;

@Deprecated
public class HttpDelete extends HttpMethodFilter {
    public HttpDelete() {
        super(HttpMethod.DELETE);
    }
}
