package balam.exof;

import balam.exof.environment.Loader;
import balam.exof.environment.MainLoader;
import balam.exof.environment.Setting;
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
		String envPath = System.getProperty("envPath", "./env");
		SystemSetting.getInstance().set(Setting.PreFix.FRAMEWORK, "envPath", envPath);
		
		 try
	        {
	        	Loader mainLoader = new MainLoader();
	        	mainLoader.load(envPath);
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
	        Operator.init();
	        Operator.start();
	}
	
	public static void stop()
	{
		Operator.stop();
	}
	
    public static void main(String[] args)
    {
       start();
    }
}
