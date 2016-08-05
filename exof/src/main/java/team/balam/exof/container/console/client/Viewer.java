package team.balam.exof.container.console.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import team.balam.exof.container.console.CommandBuilder;

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
	
	private void _getServiceList()
	{
		Client.Send(CommandBuilder.buildServiceListGetter(), result -> {
			System.out.println(result);
		});
	}
}
