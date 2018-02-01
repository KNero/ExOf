package team.balam.exof.module.service.component;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Created by smkwon on 2018-02-01.
 */
public class HttpDelete extends HttpMethodFilter {
    public HttpDelete() {
        super(HttpMethod.DELETE);
    }
}
