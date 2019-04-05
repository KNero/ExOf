package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.util.StreamUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Set;

public class BodyToObject implements Inbound {
	private static final Logger LOG = LoggerFactory.getLogger(BodyToObject.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	static {
	    JSON_MAPPER.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
    }

	private static final String JSON_CONTENT_TYPE = HttpHeaderValues.APPLICATION_JSON.toString();

	private Class<?> objectType;
	protected String charset = Charset.defaultCharset().name();

	public BodyToObject(Class<?> objectType) {
		this.objectType = objectType;
	}

	@Override
	public void execute(ServiceObject se) throws InboundExecuteException {
		if (se.getRequest() instanceof FullHttpRequest) {
			this.parseAndSetNettyRequest(se);
		} else if (se.getRequest() instanceof HttpServletRequest) {
			this.parseAndSetJettyRequest(se);
		} else {
			throw new InboundExecuteException("Request is not type that can process. " + se.getRequest());
		}
	}

	private void parseAndSetNettyRequest(ServiceObject se) throws InboundExecuteException {
		FullHttpRequest httpRequest = (FullHttpRequest) se.getRequest();
        byte[] buf = new byte[httpRequest.headers().getInt(HttpHeaderNames.CONTENT_LENGTH)];
        httpRequest.content().readBytes(buf);

		if (JSON_CONTENT_TYPE.equals(httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE))) {
            try {
                Object result = JSON_MAPPER.readValue(buf, this.objectType);
                se.addParameterValue(result);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("json transform result : {}", result);
                }
            } catch (IOException e) {
                throw new InboundExecuteException("Can't parse json body. " + new String(buf), e);
            }
		} else {
		    try {
                Object host = this.objectType.newInstance();
                se.addParameterValue(host);

                createStringQueryObject(host, new String(buf, this.charset));
            } catch (Exception e) {
                throw new InboundExecuteException("Can't parse string query body. " + new String(buf), e);
            }
        }
	}

	private void createStringQueryObject(Object host, String uri) {
        @SuppressWarnings("unchecked")
        Set<Field> fieldSet = ReflectionUtils.getAllFields(objectType);

        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        decoder.parameters().forEach((key, value) -> {
            try {
                for (Field field : fieldSet) {
                    if (field.getName().equals(key)) {
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();

                        if (value.size() == 1) {
                            String oneValue = value.get(0);

                            if ("int".equals(fieldType.getName()) || fieldType.equals(Integer.class)) {
                                field.set(host, Integer.valueOf(oneValue));
                            } else if ("long".equals(fieldType.getName()) || fieldType.equals(Long.class)) {
                                field.set(host, Long.valueOf(oneValue));
                            } else if ("float".equals(fieldType.getName()) || fieldType.equals(Float.class)) {
                                field.set(host, Float.valueOf(oneValue));
                            } else if ("double".equals(fieldType.getName()) || fieldType.equals(Double.class)) {
                                field.set(host, Double.valueOf(oneValue));
                            } else if ("byte".equals(fieldType.getName()) || fieldType.equals(Byte.class)) {
                                field.set(host, Byte.valueOf(oneValue));
                            } else if ("short".equals(fieldType.getName()) || fieldType.equals(Short.class)) {
                                field.set(host, Short.valueOf(oneValue));
                            } else {
                                field.set(host, oneValue);
                            }
                        } else {
                            field.set(host, value);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                // ignore
            }
        });
    }

	private void parseAndSetJettyRequest(ServiceObject se) throws InboundExecuteException {
		HttpServletRequest request = (HttpServletRequest) se.getRequest();
		if (!JSON_CONTENT_TYPE.equals(request.getContentType())) {
			return;
		}

		String json = null;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			StreamUtil.write(request.getInputStream(), out);

			json = URLDecoder.decode(out.toString(this.charset), this.charset);
			Object result = JSON_MAPPER.readValue(json, this.objectType);
			se.addParameterValue(result);

			if (LOG.isDebugEnabled()) {
				LOG.debug("json transform result : {}", result);
			}
		} catch (IOException e) {
			throw new InboundExecuteException("Can't parse json body. receive data: " + json, e);
		}
	}
}
