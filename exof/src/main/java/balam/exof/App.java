package balam.exof;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.environment.EnvKey;
import balam.exof.environment.LoadEnvException;
import balam.exof.environment.Loader;
import balam.exof.environment.MainLoader;
import balam.exof.environment.SystemSetting;

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
		
		try
		{
			Loader mainLoader = new MainLoader();
			mainLoader.load(envPath);
		}
		catch(LoadEnvException e)
		{
			Logger logger = LoggerFactory.getLogger(App.class);
			logger.error("Loader error occurred.", e);
			
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
