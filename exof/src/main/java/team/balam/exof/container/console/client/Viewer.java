package team.balam.exof.container.console.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Viewer 
{
	public BufferedReader standardReader = null;
	private String selectMenuNumber;
	
	public void start() throws Exception
	{
		System.out.println("   =======             ===");
		System.out.println("   |                  =   =     ==");
		System.out.println("   =======   =   =   =     =   |");
		System.out.println("   |           =      =   =  =====");
		System.out.println("   =======   =   =     ===     |");
		System.out.println();
		System.out.println("+++ Welcom ExOf console monitoring +++");
		
		this.standardReader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true)
		{
			try
			{
				if(this._showOneLevelQuestion())
				{
					while(true)
					{
						if(this._showTwoLevelQuestion())
						{
							Executor.execute(this.selectMenuNumber);
						}
						else
						{
							break;
						}
					}
				}
				else
				{
					break;
				}
			}
			catch(TerminateException e)
			{
				break;
			}
		}
		
		if(this.standardReader != null)
		{
			this.standardReader.close();
		}
		
		System.out.println("+++ Bye Bye +++");
	}
	
	private boolean _showOneLevelQuestion() throws IOException
	{
		System.out.println("\n(1)get info   (2)set info   (3)quit");
		
		while(true)
		{
			String cmd = this.standardReader.readLine();
			if(cmd == null)
			{
				throw new TerminateException();
			}
			
			switch(cmd)
			{
				case Menu.OneLevel.GET:
					this.selectMenuNumber = cmd;
					return true;
					
				case Menu.OneLevel.SET:
					System.out.println("Not support yet.");
					return true;
					
				case Menu.QUIT:
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
		System.out.println("\n(1)service list   (2)schedule list   (3)quit");
		
		while(true)
		{
			String cmd = this.standardReader.readLine();
			if(cmd == null)
			{
				throw new TerminateException();
			}
			
			int menu = Integer.parseInt(cmd);
			if(1 <= menu && menu <= 2)
			{
				this.selectMenuNumber = cmd;
				return true;
			}
			else if(Menu.QUIT.equals(cmd))
			{
				return false;
			}
			else
			{
				System.out.println("\n(1)service list   (2)schedule list   (3)quit");
				System.out.println("Enter numbaer 1 ~ 3");
			}
		}
	}
}
