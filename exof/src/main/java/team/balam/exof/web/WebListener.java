package team.balam.exof.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import team.balam.exof.App;
import team.balam.exof.environment.EnvKey;

public class WebListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		String configPath = arg0.getServletContext().getInitParameter("exof.configPath");
		if(configPath != null && configPath.trim().length() > 0)
		{
			System.setProperty(EnvKey.HOME, configPath.trim());
		}
		
		App.start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		
	}
}
