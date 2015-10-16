package balam.exof;

import balam.exof.environment.Loader;
import balam.exof.environment.MainLoader;

/**
 * 
 * @author kwonsm
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        try
        {
        	Loader mainLoader = new MainLoader();
        	mainLoader.load();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
}
