package team.balam.exof.module.service.component;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.util.HttpResponseBuilder;

/**
 * Created by smkwon on 2018-02-01.
 */
public class HttpMethodFilter implements Inbound {
    private HttpMethod method;

    HttpMethodFilter(HttpMethod method) {
        this.method = method;
    }

    @Override
    public void execute(ServiceObject _se) throws InboundExecuteException {
        HttpRequest request = (HttpRequest) _se.getRequest();
        if (!this.method.equals(request.method())) {
            FullHttpResponse response = HttpResponseBuilder.buildNotImplemented("Http method is must " + this.method.name());
            RequestContext.writeAndFlushResponse(response);

            throw new InboundExecuteException("Bad http method. HTTP method is must " + this.method.name() + ". request path: "
                    + _se.getServicePath());
        }
    }
}