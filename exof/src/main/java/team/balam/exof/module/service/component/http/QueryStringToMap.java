package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.component.Inbound;
import team.balam.exof.module.service.component.InboundExecuteException;

import javax.servlet.http.HttpServletRequest;
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
	private String charset = Charset.defaultCharset().name();

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void execute(ServiceObject se) throws InboundExecuteException {
		if (se.getRequest() instanceof HttpRequest) {
			this.setParameterForNetty(se);
		} else {
			this.setParameterForJetty(se);
		}
	}

	private void setParameterForNetty(ServiceObject se) {
		HttpRequest request = (HttpRequest)se.getRequest();
		setParameter(se, request.uri());
	}

	private void setParameterForJetty(ServiceObject se) throws InboundExecuteException {
		if (!(se.getRequest() instanceof HttpServletRequest)) {
			throw new InboundExecuteException("Request is not type that can process. " + se.getRequest());
		}

		HttpServletRequest request = ((HttpServletRequest)se.getRequest());
		String queryString = request.getQueryString();

		if (!StringUtil.isNullOrEmpty(queryString)) {
			try {
				setParameter(se, URLDecoder.decode("?" + queryString, this.charset));
			} catch (UnsupportedEncodingException var4) {
				LOG.error("Encoding is unsupported. " + this.charset, var4);
			}
		}
	}

	private static void setParameter(ServiceObject se, String uri) {
		Map<String, Object> param = new HashMap<>();
		se.addParameterValue(param);

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
