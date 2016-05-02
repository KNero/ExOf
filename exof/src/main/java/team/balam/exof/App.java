package team.balam.exof;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.LoadEnvException;
import team.balam.exof.environment.Loader;
import team.balam.exof.environment.MainLoader;
import team.balam.exof.environment.SystemSetting;

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
