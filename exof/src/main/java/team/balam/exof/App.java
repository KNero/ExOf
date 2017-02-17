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
	private static volatile boolean isShutdown = false;
	
	public static void start()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				App.isShutdown = true;
				
				Operator.stop();
			}
		});
		
		String envPath = System.getProperty(EnvKey.HOME, "./env");
		SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, EnvKey.HOME, envPath);
		
		Logger logger = LoggerFactory.getLogger(App.class);
		
		try
		{
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
        
        //main thread에서 worker들을 검사한다.
        while(! App.isShutdown)
        {
        	ThreadWorkerRegister.getInstance().check();
        	
        	try 
        	{
				Thread.sleep(1000);
			}
        	catch(InterruptedException e) 
        	{
			}
        }
	}
	
    public static void main(String[] args)
    {
    	App.start();
    }
}
