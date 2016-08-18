package team.balam.exof.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.service.Service;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceProvider;

public class WebServlet extends HttpServlet
{
	private static final long serialVersionUID = 5734814584752384567L;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());	
	private HttpServletTransform httpServletTransform;
	
	@Override
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		
		String transformName = config.getInitParameter("httpServletTransform");
		if(transformName != null && transformName.trim().length() > 0)
		{
			try 
			{
				Object transform = Class.forName(transformName).newInstance();
				this.httpServletTransform = (HttpServletTransform)transform;
			} 
			catch(Exception e) 
			{
				this.logger.error("Can not create httpServletTransform.", e);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		this._executeService(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		this._executeService(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		this._executeService(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		this._executeService(req, resp);
	}
	
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		this._executeService(req, resp);
	}
	
	@Override
	protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException 
	{
		this._executeService(arg0, arg1);
	}
	
	@Override
	protected void doTrace(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException 
	{
		this._executeService(arg0, arg1);
	}
	private void _executeService(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		HttpServletMessage msg = new HttpServletMessage();
		msg.setRequest(req);
		msg.setResponse(resp);
		
		ServiceObject serviceObject = null;
		
		if(this.httpServletTransform != null)
		{
			try 
			{
				serviceObject = this.httpServletTransform.transform(msg);
			} 
			catch(Exception e) 
			{
				this.logger.error("Can not transform http message.", e);
				
				throw new ServletException(e);
			}
			
			if(serviceObject == null)
			{
				this.logger.error("ServiceObject is null");
				
				throw new UnavailableException(req.getMethod() + " is not available");
			}
		}
		else
		{
			serviceObject = new ServiceObject(req.getServletPath());
			serviceObject.setRequest(msg);
		}
		
		long start = System.currentTimeMillis();
		
		try 
		{
			Service service = ServiceProvider.lookup(serviceObject.getServicePath());
			if(service == null)
			{
				throw new NullPointerException("Service is null");
			}
			
			service.call(serviceObject);
		} 
		catch(Exception e)
		{
			this.logger.error("Can not execute service.", e);
			
			throw new ServletException(e);
		}
		finally
		{
			if(this.logger.isInfoEnabled())
			{
				long end = System.currentTimeMillis();
				this.logger.info("Service[{}] is completed. Elapsed : {} ms", serviceObject.getServicePath(), end - start);
			}
		}
	}
}
