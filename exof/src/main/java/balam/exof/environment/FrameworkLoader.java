package balam.exof.environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

import balam.exof.util.CollectionUtil;

/**
 * framework.yaml을 SystemSetting으로 저장.
 * @author kwonsm
 *
 */
public class FrameworkLoader implements Loader
{
	@Override
	@SuppressWarnings("unchecked")
	public void load(String _envPath) throws LoadEnvException 
	{
		FileInputStream frameworkFile = null;
		
		try
		{
			frameworkFile = new FileInputStream(_envPath + "/" + "framework.yaml");
			
			Yaml yamlParser = new Yaml();
			Map<String, ?> root = (Map<String, ?>)yamlParser.load(frameworkFile);
			Map<String, ?> fw = (Map<String, ?>)root.get(EnvKey.Framework.FRAMEWORK);
			
			List<String> container = (List<String>)fw.get(EnvKey.Framework.CONTAINER);
			SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.CONTAINER, container);
			
			Map<String, Object> scheduler = (Map<String, Object>)fw.get(EnvKey.Framework.SCHEDULER);
			Properties sp = new Properties();
			SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER, sp);
			
			CollectionUtil.doIterator(scheduler.keySet(), _key -> {
				Object value = scheduler.get(_key).toString();
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
