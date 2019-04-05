package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.http.HttpHeaders;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Outbound;
import team.balam.exof.module.service.component.OutboundExecuteException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class ResponseToJson implements Outbound<Object, Object> {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    static {
        JSON_MAPPER.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private static final String ACCEPT_JSON = HttpHeaderValues.APPLICATION_JSON.toString();

    @Override
    public Object execute(Object result) throws OutboundExecuteException {
        ServiceObject serviceObject = RequestContext.get(RequestContext.Key.SERVICE_OBJECT);
        Object request = serviceObject.getRequest();

        try {
            if (request instanceof FullHttpRequest) {
                FullHttpRequest httpRequest = (FullHttpRequest) request;
                String accept = httpRequest.headers().get(HttpHeaderNames.ACCEPT);

                if (ACCEPT_JSON.equals(accept)) {
                    log.info("convert object to http response json body.");
                    return JSON_MAPPER.writeValueAsString(result);
                }
            } else if (request instanceof HttpServletRequest) {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                String accept = httpServletRequest.getHeader(HttpHeaders.ACCEPT);

                if (ACCEPT_JSON.equals(accept)) {
                    log.info("convert object to http response json body.");
                    return JSON_MAPPER.writeValueAsString(result);
                }
            } else {
                return JSON_MAPPER.writeValueAsString(result);
            }
        } catch (Exception e) {
            throw new OutboundExecuteException("File to Object to Json", e);
        }

        return result;
    }
}
