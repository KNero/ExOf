package team.balam.exof.module.was;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceWrapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebServlet extends HttpServlet {
	private static final Logger LOG  = LoggerFactory.getLogger(WebServlet.class);

	private ServicePathExtractor servicePathExtractor;

	@Override
	public void init() {
		String servicePathExtractorClassName = this.getInitParameter("servicePathExtractor");
		if (!StringUtil.isNullOrEmpty(servicePathExtractorClassName)) {
			try {
				this.servicePathExtractor = (ServicePathExtractor) Class.forName(servicePathExtractorClassName).newInstance();
			} catch (Exception e) {
				LOG.error("Can't create servicePathExtractor instance.", e);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		this._execute(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		this._execute(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		this._execute(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		this._execute(req, resp);
	}

	private void _execute(HttpServletRequest req, HttpServletResponse resp) {
		RequestContext.set(RequestContext.Key.HTTP_SERVLET_REQ, req);
		RequestContext.set(RequestContext.Key.HTTP_SERVLET_RES, resp);

		String servicePath = req.getPathInfo();
		if (this.servicePathExtractor != null) {
			servicePath = this.servicePathExtractor.extract(req);
		}

		ServiceObject serviceObject = new ServiceObject(servicePath);
		serviceObject.setRequest(req);

		RequestContext.set(RequestContext.Key.SERVICE_OBJECT, serviceObject);

		try {
			ServiceWrapper service = ServiceProvider.lookup(servicePath);
			if (service.isInternal()) {
				LOG.error("Service is internal. path:{}, class:{}", servicePath, service.getHost());
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Can not call internal service.");
				return;
			}

			long start = System.currentTimeMillis();

			service.call(serviceObject);

			if(LOG.isInfoEnabled()) {
				long end = System.currentTimeMillis();
				LOG.info("Service[{}] is completed. Elapsed : {} ms", servicePath, end - start);
			}
		} catch(Exception e) {
			LOG.error("An error occurred during service execution.", e);
		}
	}
}
