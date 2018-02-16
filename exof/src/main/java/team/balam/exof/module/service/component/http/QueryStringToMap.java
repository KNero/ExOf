package team.balam.exof.module.service.component.http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
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
	protected String charset = Charset.defaultCharset().name();

	@Override
	public void execute(ServiceObject _se) throws InboundExecuteException {
		if (_se.getRequest() instanceof HttpRequest) {
			_setParameter(_se, ((HttpRequest) _se.getRequest()).uri());
		} else if (_se.getRequest() instanceof HttpServletRequest) {
			String queryString = "?" + ((HttpServletRequest) _se.getRequest()).getQueryString();
			try {
				_setParameter(_se, URLDecoder.decode(queryString, this.charset));
			} catch (UnsupportedEncodingException e) {
				LOG.error("Encoding is unsupported. " + this.charset, e);
			}
		} else {
			throw new InboundExecuteException("Request is not type that can process. " + _se.getRequest());
		}
	}

	private static void _setParameter(ServiceObject _se, String _uri) {
		Map<String, Object> param = new HashMap<>();
		_se.setServiceParameter(new Object[]{param});

		if (_uri != null) {
			QueryStringDecoder decoder = new QueryStringDecoder(_uri);
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

		LOG.info("query string value : {}", param);

	}
}
