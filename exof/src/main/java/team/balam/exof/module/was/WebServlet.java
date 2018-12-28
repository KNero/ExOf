package team.balam.exof.module.was;

import io.netty.util.internal.StringUtil;
import org.apache.ibatis.ognl.ObjectMethodAccessor;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.ExternalClassLoader;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceWrapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * ExofRequestFilter 를 사용해 주세요.
 */
@Deprecated
public class WebServlet extends HttpServlet {
	private static final Logger LOG  = LoggerFactory.getLogger(WebServlet.class);

	private ServicePathExtractor servicePathExtractor;
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Override
	public void init() {
		setServicePathExtractor(this.getInitParameter("servicePathExtractor"));
	}

	void setServicePathExtractor(String servicePathExtractorClassName) {
		if (!StringUtil.isNullOrEmpty(servicePathExtractorClassName)) {
			try {
				servicePathExtractor = (ServicePathExtractor) ExternalClassLoader.loadClass(servicePathExtractorClassName).newInstance();
			} catch (Exception e) {
				LOG.error("Can't create servicePathExtractor instance.", e);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			this.execute(req, resp);
		} catch (Exception e) {
			LOG.error("An error occurred during service execution.", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			this.execute(req, resp);
		} catch (Exception e) {
			LOG.error("An error occurred during service execution.", e);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		try {
			this.execute(req, resp);
		} catch (Exception e) {
			LOG.error("An error occurred during service execution.", e);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		try {
			this.execute(req, resp);
		} catch (Exception e) {
			LOG.error("An error occurred during service execution.", e);
		}
	}

	void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		RequestContext.set(RequestContext.Key.HTTP_SERVLET_REQ, req);
		RequestContext.set(RequestContext.Key.HTTP_SERVLET_RES, resp);

		String servicePath = req.getRequestURI();
		if (servicePathExtractor != null) {
			servicePath = servicePathExtractor.extract(req);
		}

		ServiceObject serviceObject = new ServiceObject(servicePath);
		serviceObject.setRequest(req);
		serviceObject.setServiceGroupId(req.getMethod().toUpperCase());

		RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

		ServiceWrapper service = ServiceProvider.lookup(serviceObject);
		if (service.isInternal()) {
			LOG.error("Service is internal. path:{}, class:{}", servicePath, service.getHost());
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Can not call internal service.");
			return;
		}

		long start = System.currentTimeMillis();

		Object response = service.call(serviceObject);

		if (response != null) {
			PrintWriter out = resp.getWriter();

			if (response instanceof String) {
				out.write((String) response);
				out.flush();
			} else {
				JSON_MAPPER.writeValue(out, response);
			}
		}

		if(LOG.isInfoEnabled()) {
			long end = System.currentTimeMillis();
			LOG.info("Service[{}] is completed. Elapsed : {} ms", servicePath, end - start);
		}
	}
}
