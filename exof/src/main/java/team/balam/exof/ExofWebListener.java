package team.balam.exof;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import team.balam.exof.environment.EnvKey;

public class ExofWebListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		String configPath = arg0.getServletContext().getInitParameter("exof.configPath");
		if(configPath != null && configPath.trim().length() > 0)
		{
			System.setProperty(EnvKey.ENV_PATH, configPath.trim());
		}
		
		App.start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		
	}
}
