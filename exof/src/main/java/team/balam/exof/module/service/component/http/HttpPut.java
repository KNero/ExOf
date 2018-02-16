package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Created by smkwon on 2018-02-01.
 */
public class HttpPut extends HttpMethodFilter {
    public HttpPut() {
        super(HttpMethod.PUT);
    }
}
