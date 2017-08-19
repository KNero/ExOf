package team.balam.exof.environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

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
			
			fw.keySet().forEach(_key -> {
				if(EnvKey.Framework.SCHEDULER.equals(_key))
				{
					Properties sp = new Properties();
					SystemSetting.setFramework(EnvKey.Framework.SCHEDULER, sp);
					
					Map<String, Object> scheduler = (Map<String, Object>)fw.get(EnvKey.Framework.SCHEDULER);
					scheduler.keySet().forEach(_quartzKey -> {
						Object value = scheduler.get(_quartzKey).toString();
						sp.put(_quartzKey, value);
					});
				}
				else
				{
					Object values = fw.get(_key);
					
					if(EnvKey.Framework.EXTERNAL.equals(_key)) {
						SystemSetting.setExternal(values);
					}
					else if(values instanceof Map)
					{
						Map<String, ?> mapValues = (Map<String, ?>)values;
						mapValues.keySet().forEach(_mapKey -> {
							String key = _key + "." + _mapKey;
							Object value = mapValues.get(_mapKey);
							SystemSetting.setFramework(key, value);
						});
					}
					else
					{
						SystemSetting.setFramework(_key, values);
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
