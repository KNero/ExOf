package team.balam.exof.container.console.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;



import team.balam.exof.ConstantKey;
import team.balam.exof.container.console.CommandBuilder;
import team.balam.exof.util.CollectionUtil;


public class Viewer 
{
	public BufferedReader standardReader = new BufferedReader(new InputStreamReader(System.in));
	
	private String selectMenuNumber;
	
	public void start() throws Exception
	{
		System.out.println("+++ Welcom ExOf console monitoring +++");
		
		while(true)
		{
			if(this._showOneLevelQuestion())
			{
				if(this._showTwoLevelQuestion())
				{
					this._executeCommand();
				}
			}
			else
			{
				break;
			}
		}
		
		System.out.println("+++ Bye Bye +++");
	}
	
	private boolean _showOneLevelQuestion() throws IOException
	{
		System.out.println("\n(1)get info   (2)set info   (3)quit");
		
		while(true)
		{
			String cmd = this.standardReader.readLine();
			
			switch(cmd)
			{
				case "1":
					this.selectMenuNumber = cmd;
					return true;
					
				case "2":
					System.out.println("Not support yet.");
					return false;
					
				case "3":
					return false;
					
				default:
					System.out.println("\n(1)get info   (2)set info   (3)quit");
					System.out.println("Enter numbaer 1 or 2 or 3");
					break;
			}
		}
	}
	
	private boolean _showTwoLevelQuestion() throws IOException
	{
		System.out.println("\n(1)service list   (2)quit");
		
		while(true)
		{
			String cmd = this.standardReader.readLine();
			
			switch(cmd)
			{
				case "1":
					this.selectMenuNumber = cmd;
					return true;
					
				case "2":
					return false;
					
				default:
					System.out.println("\n(1)service list   (2)quit");
					System.out.println("Enter numbaer 1 or 2");
					break;
			}
		}
	}
	
	private void _executeCommand()
	{
		switch(this.selectMenuNumber)
		{
			case "1":
				this._getServiceList();
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void _getServiceList()
	{
		Client.Send(CommandBuilder.buildServiceListGetter(), _result -> {
			String resultValue = (String)_result.get(ConstantKey.RESULT_KEY);
			if(resultValue != null)
			{
				System.out.println(resultValue);
			}
			else
			{
				_result.forEach((_key, _value) -> {
					Map<String, String> valueMap = (Map<String, String>)_value;
					System.out.println("Directory path : " + _key);
					System.out.println("Class : " + valueMap.get(ConstantKey.CLASS_KEY));
					System.out.println("Service list");
					
					CollectionUtil.doIterator(valueMap.keySet(), _valueKey -> {
						if(! ConstantKey.CLASS_KEY.equals(_valueKey))
						{
							System.out.println("  - " + _valueKey + " : " + valueMap.get(_valueKey));
						}
					});
					
					System.out.println();
				});
			}
		});
	}
}
