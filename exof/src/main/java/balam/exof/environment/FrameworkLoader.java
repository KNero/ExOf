package balam.exof.environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

import balam.exof.util.Function;

public class FrameworkLoader implements Loader
{
	@Override
	@SuppressWarnings("unchecked")
	public void load() throws LoadEnvException 
	{
		FileInputStream frameworkFile = null;
		
		try
		{
			String home = SystemSetting.getInstance().getString(Setting.PreFix.FRAMEWORK, "home");
			frameworkFile = new FileInputStream(home + "/" + "framework.yaml");
			
			Yaml yamlParser = new Yaml();
			Map<String, ?> root = (Map<String, ?>)yamlParser.load(frameworkFile);
			Map<String, ?> fw = (Map<String, ?>)root.get(EnvKey.Framework.FRAMEWORK);
			
			Map<String, String> scheduler = (Map<String, String>)fw.get(EnvKey.Framework.SCHEDULER);
			Properties sp = new Properties();
			SystemSetting.getInstance().set(Setting.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER, sp);
			
			Function.doIterator(scheduler.keySet(), (_key) -> {
				String value = scheduler.get(_key);
				sp.put(_key, value);
			});
		}
		catch(Exception e)
		{
			throw new LoadEnvException("framework.yaml", e);
		}
		finally
		{
			try
			{
				if(frameworkFile != null) frameworkFile.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
