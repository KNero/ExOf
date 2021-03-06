package team.balam.exof;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;

import java.io.File;

public class ExofWebListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		String configPath = arg0.getServletContext().getInitParameter("exof.home");
		if(configPath != null && configPath.trim().length() > 0) {
			SystemSetting.setFramework(EnvKey.HOME, configPath.trim());
		} else {
			String currentPath = new File(".").getAbsolutePath();
			SystemSetting.setFramework(EnvKey.HOME, currentPath);
		}
		
		App.start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
}
