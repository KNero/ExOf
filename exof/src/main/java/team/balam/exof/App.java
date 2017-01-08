package team.balam.exof;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.Loader;
import team.balam.exof.environment.MainLoader;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.util.FileClassLoader;

/**
 * 
 * @author kwonsm
 *
 */
public class App 
{
	public static void start()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				Operator.stop();
			}
		});
		
		String envPath = System.getProperty(EnvKey.HOME, "./env");
		SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, EnvKey.HOME, envPath);
		
		Logger logger = LoggerFactory.getLogger(App.class);
		
		try
		{
			if(new File("./lib/external").exists())
			{
				FileClassLoader.loadJar("./lib/external");
			}
			
			File classes = new File("./classes");
			if(classes.exists())
			{
				FileClassLoader.loadFileOrDirectory(classes);
			}
			
			Loader mainLoader = new MainLoader();
			mainLoader.load(envPath);
		}
		catch(Exception e) 
    	{
    		if(e instanceof LoadEnvException)
    		{
    			logger.error("Loader error occurred.", e);
    		}
    		else
    		{
    			logger.error("Fail to load Custom jar.", e);
    		}
    		
    		e.printStackTrace();
		}
    	
	        
        Operator.init();
        Operator.start();
	}
	
    public static void main(String[] args)
    {
    	start();
    }
}
