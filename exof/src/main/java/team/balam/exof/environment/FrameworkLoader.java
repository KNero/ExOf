package team.balam.exof.environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;
import org.yaml.snakeyaml.Yaml;

import team.balam.exof.util.CollectionUtil;

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
			
			CollectionUtil.doIterator(fw.keySet(), _key -> {
				if(_key.equals(EnvKey.Framework.INIT_LOG))
				{
					boolean isInitLog = (boolean)fw.get(EnvKey.Framework.INIT_LOG);
					if(isInitLog)
					{
						DOMConfigurator.configure(_envPath + "/" + "Log4j.xml");
					}
				}
				else if(_key.equals(EnvKey.Framework.SCHEDULER))
				{
					Properties sp = new Properties();
					SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, EnvKey.Framework.SCHEDULER, sp);
					
					Map<String, Object> scheduler = (Map<String, Object>)fw.get(EnvKey.Framework.SCHEDULER);
					CollectionUtil.doIterator(scheduler.keySet(), _quartzKey -> {
						Object value = scheduler.get(_quartzKey).toString();
						sp.put(_quartzKey, value);
					});
				}
				else
				{
					Object values = fw.get(_key);
					
					if(values instanceof Map)
					{
						Map<String, ?> mapValues = (Map<String, ?>)values;
						CollectionUtil.doIterator(mapValues.keySet(), _mapKey -> {
							String key = _key + "." + _mapKey;
							Object value = mapValues.get(_mapKey);
							SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, key, value);
						});
					}
					else
					{
						SystemSetting.getInstance().set(EnvKey.PreFix.FRAMEWORK, _key, values);
					}
				}
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
