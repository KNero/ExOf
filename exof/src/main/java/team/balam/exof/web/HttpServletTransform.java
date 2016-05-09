package team.balam.exof.web;

import team.balam.exof.listener.handler.transform.ServiceObjectTransform;
import team.balam.exof.service.ServiceObject;

public abstract class HttpServletTransform implements ServiceObjectTransform<HttpServletMessage>
{
	@Override
	public ServiceObject transform(HttpServletMessage _msg) throws Exception 
	{
		switch(_msg.getMethod().toUpperCase())
		{
			case "POST":
				return this.doPost(_msg);
			
			case "GET":
				return this.doGet(_msg);
				
			case "DELETE":
				return this.doDelete(_msg);
				
			case "PUT":
				return this.doPut(_msg);
				
			case "HEAD":
				return this.doHead(_msg);
				
			case "OPTIONS":
				return this.doOptions(_msg);
			
			default:
				throw new Exception("Possible Mehtod[POST, GET, DELETE, PUT, HEAD, OPTIONS], Request Method[" + _msg.getMethod() + "]");
		}
	}
	
	protected ServiceObject doPost(HttpServletMessage _msg) throws Exception 
	{
		return null;
	}
	
	protected ServiceObject doGet(HttpServletMessage _msg) throws Exception  
	{
		return null;
	}
	
	protected ServiceObject doDelete(HttpServletMessage _msg) throws Exception  
	{
		return null;
	}
	
	protected ServiceObject doPut(HttpServletMessage _msg) throws Exception  
	{
		return null;
	}
	
	protected ServiceObject doHead(HttpServletMessage _msg) throws Exception  
	{
		return null;
	}
	
	protected ServiceObject doOptions(HttpServletMessage _msg) throws Exception  
	{
		return null;
	}
}
