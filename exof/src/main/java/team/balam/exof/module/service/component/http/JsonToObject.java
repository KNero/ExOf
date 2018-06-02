package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.util.StreamUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

public abstract class JsonToObject implements Inbound {
	private static final Logger LOG = LoggerFactory.getLogger(JsonToObject.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private Class<?> objectType;
	protected String charset = Charset.defaultCharset().name();

	protected JsonToObject(Class<?> objectType) {
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

		try {
			Object result = JSON_MAPPER.readValue(buf, this.objectType);
			se.setServiceParameter(result);

			LOG.info("json transform result : {}", result);
		} catch (IOException e) {
			throw new InboundExecuteException("Can't parse json body. " + new String(buf), e);
		}
	}

	private void parseAndSetJettyRequest(ServiceObject se) throws InboundExecuteException {
		HttpServletRequest request = (HttpServletRequest) se.getRequest();
		String json = null;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			StreamUtil.write(request.getInputStream(), out);

			json = URLDecoder.decode(out.toString(this.charset), this.charset);
			Object result = JSON_MAPPER.readValue(json, this.objectType);
			se.setServiceParameter(result);
		} catch (IOException e) {
			throw new InboundExecuteException("Can't parse json body. receive data: " + json, e);
		}
	}
}
