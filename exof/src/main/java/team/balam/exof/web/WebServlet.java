package team.balam.exof.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.service.Service;
import team.balam.exof.service.ServiceObject;
import team.balam.exof.service.ServiceProvider;

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
				this.logger.error("Can not create httpServletTransform.");
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		super.doPost(req, resp);
		
		this._executeService(req, resp);
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
			}
		}
		else
		{
			serviceObject = new ServiceObject(req.getPathInfo());
			serviceObject.setRequest(msg);
		}
		
		try 
		{
			Service service = ServiceProvider.lookup(serviceObject.getServicePath());
			service.call(serviceObject);
		} 
		catch(Exception e)
		{
			this.logger.error("Can not execute service.", e);
		}
	}
}
