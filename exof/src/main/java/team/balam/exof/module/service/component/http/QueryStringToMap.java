package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.eclipse.jetty.http.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;
import team.balam.exof.util.StreamUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * http url 의 query string 을 map 으로 변환하여 전달한다.
 * value 가 하나일 경우 : String
 * value array 형태일 경우 : List<String>
 * value 가 없을 경우 : 빈 문자열
 */
public class QueryStringToMap implements Inbound {
	private static final Logger LOG = LoggerFactory.getLogger(QueryStringToMap.class);
	protected String charset = Charset.defaultCharset().name();

	public void execute(ServiceObject se) throws InboundExecuteException {
		if (se.getRequest() instanceof HttpRequest) {
			this.setParameterForNetty(se);
		} else {
			this.setParameterForJetty(se);
		}
	}

	private void setParameterForNetty(ServiceObject se) {
		HttpRequest request = (HttpRequest)se.getRequest();
		if (request.method().name().equals(HttpMethod.POST.name()) && request instanceof FullHttpRequest) {
			setParameter(se, ((FullHttpRequest) request).content().toString(Charset.forName(this.charset)));
		} else {
			setParameter(se, request.uri());
		}
	}

	private void setParameterForJetty(ServiceObject se) throws InboundExecuteException {
		if (!(se.getRequest() instanceof HttpServletRequest)) {
			throw new InboundExecuteException("Request is not type that can process. " + se.getRequest());
		}

		HttpServletRequest request = ((HttpServletRequest)se.getRequest());
		String queryString = "";

		if (request.getMethod().equals(HttpMethods.POST)) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				StreamUtil.write(request.getInputStream(), out);
				queryString = "?" + out.toString();
			} catch (IOException e) {
				LOG.error("Can't read request body.", e);
			}
		} else {
			queryString = "?" + request.getQueryString();
		}

		try {
			setParameter(se, URLDecoder.decode(queryString, this.charset));
		} catch (UnsupportedEncodingException var4) {
			LOG.error("Encoding is unsupported. " + this.charset, var4);
		}
	}

	private static void setParameter(ServiceObject se, String uri) {
		Map<String, Object> param = new HashMap<>();
		se.setServiceParameter(param);
		if (uri != null) {
			QueryStringDecoder decoder = new QueryStringDecoder(uri);
			decoder.parameters().forEach((key, value) -> {
				if (value.size() == 1) {
					param.put(key, value.get(0));
				} else if (value.size() > 1) {
					param.put(key, value);
				} else {
					param.put(key, "");
				}

			});
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("QueryString: {}", param);
		}
	}
}
