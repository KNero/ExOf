package team.balam.exof.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServletMessage 
{
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public HttpServletRequest getRequest() 
	{
		return request;
	}
	
	public void setRequest(HttpServletRequest request) 
	{
		this.request = request;
	}
	
	public HttpServletResponse getResponse() 
	{
		return response;
	}
	
	public void setResponse(HttpServletResponse response) 
	{
		this.response = response;
	}
	
	public String getMethod()
	{
		return this.request.getMethod();
	}
	
	public String getRequestPath()
	{
		return this.request.getPathInfo();
	}
}
