package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public abstract class JsonToObject implements Inbound {
	private static final Logger LOG = LoggerFactory.getLogger(JsonToObject.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private Class<?> objectType;

	protected JsonToObject(Class<?> _objectType) {
		this.objectType = _objectType;
	}

	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException {
		if (_se.getRequest() instanceof FullHttpRequest) {
			this._parseAndSetNettyRequest(_se);
		} else if (_se.getRequest() instanceof HttpServletRequest) {
			this._parseAndSetJettyRequest(_se);
		} else {
			throw new InboundExecuteException("Request is not type that can process. " + _se.getRequest());
		}
	}

	private void _parseAndSetNettyRequest(ServiceObject _se) throws InboundExecuteException {
		FullHttpRequest httpRequest = (FullHttpRequest) _se.getRequest();
		byte[] buf = new byte[httpRequest.headers().getInt(HttpHeaderNames.CONTENT_LENGTH)];
		httpRequest.content().readBytes(buf);

		try {
			Object result = JSON_MAPPER.readValue(buf, this.objectType);
			_se.setServiceParameter(new Object[]{result});

			LOG.info("json transform result : {}", result.toString());
		} catch (IOException e) {
			throw new InboundExecuteException("Can't parse json body. " + new String(buf), e);
		}
	}

	private void _parseAndSetJettyRequest(ServiceObject _se) throws InboundExecuteException {
		HttpServletRequest request = (HttpServletRequest) _se.getRequest();
		InputStream bodyIn = null;

		try {
			bodyIn = request.getInputStream();
			Object result = JSON_MAPPER.readValue(bodyIn, this.objectType);
			_se.setServiceParameter(new Object[]{result});

			LOG.info("json transform result : {}", result.toString());
		} catch (IOException e) {
			throw new InboundExecuteException("Can't parse json body. ", e);
		} finally {
			if (bodyIn != null) {
				try {
					bodyIn.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
}
