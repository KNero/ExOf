package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.util.HttpResponseBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    	if (_se.getRequest() instanceof HttpRequest) {
		    HttpRequest request = (HttpRequest) _se.getRequest();
		    if (!this.method.equals(request.method())) {
			    FullHttpResponse response = HttpResponseBuilder.buildNotImplemented("Http method is must " + this.method.name());
			    RequestContext.writeAndFlushResponse(response);

			    throw this.throwException(_se.getServicePath());
		    }
	    } else if (_se.getRequest() instanceof HttpServletRequest) {
    		HttpServletRequest request = (HttpServletRequest) _se.getRequest();
    		if (!this.method.name().equals(request.getMethod())) {
    			HttpServletResponse response = RequestContext.get(RequestContext.Key.HTTP_SERVLET_RES);
    			try {
				    response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Http method is must " + this.method.name());
			    } catch (IOException e) {
    				throw new InboundExecuteException("Fail to send response.", e);
			    }

			    throw this.throwException(_se.getServicePath());
		    }
	    }
    }

    private InboundExecuteException throwException(String _servicePath) {
    	return new InboundExecuteException("Bad http method. HTTP method is must " + this.method.name()
			    + ". request path: " + _servicePath);
    }
}