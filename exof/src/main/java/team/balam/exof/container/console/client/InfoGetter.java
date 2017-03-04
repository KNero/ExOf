package team.balam.exof.container.console.client;

import java.util.List;
import java.util.Map;

import team.balam.exof.container.console.Command;
import team.balam.exof.container.console.CommandBuilder;
import team.balam.exof.environment.EnvKey;

public class InfoGetter
{
	@SuppressWarnings("unchecked")
	public void getServiceList()
	{
		Client.send(CommandBuilder.buildServiceListGetter(), _result -> {
			if(this._isExistData(_result))
			{
				_result.forEach((_key, _value) -> {
					Map<String, Object> valueMap = (Map<String, Object>)_value;
					System.out.println("Directory path : " + _key);
					System.out.println("Class : " + valueMap.get(Command.Key.CLASS));
					System.out.println("Service list");
					
					valueMap.keySet().forEach(_valueKey -> {
						if(! Command.Key.CLASS.equals(_valueKey))
						{
							if(! _valueKey.endsWith(EnvKey.Service.SERVICE_VARIABLE))
							{
								System.out.println("  - " + _valueKey + "(method name : " + valueMap.get(_valueKey) + ")");
								
								Map<String, String> variables = (Map<String, String>)valueMap.get(_valueKey + EnvKey.Service.SERVICE_VARIABLE);
								variables.keySet().forEach(_name -> {
									System.out.println("   -- " + _name + " : " + variables.get(_name));
								});
							}
						}
					});
					
					System.out.println();
				});
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getScheduleList()
	{
		Client.send(CommandBuilder.buildScheduleListGetter(), _result -> {
			if(this._isExistData(_result))
			{
				List<String> list = (List<String>)_result.get("list");
				list.forEach(scheduleName -> {
					System.out.println("- " + scheduleName);
				});
			}
		});
	}
	
	private boolean _isExistData(Map<String, Object> _result)
	{
		String resultValue = (String)_result.get(Command.Key.RESULT);
		if(resultValue != null)
		{
			System.out.println(resultValue);
			return false;
		}
		else
		{
			return true;
		}
	}
}
