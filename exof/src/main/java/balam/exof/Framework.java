package balam.exof;

import balam.exof.environment.FileModifyChecker;



public class Framework implements Container
{
	private FileModifyChecker fileModifyChecker;
	
	@Override
	public String getName() 
	{
		return "Framework";
	}

	@Override
	public void start() throws Exception 
	{
		this.fileModifyChecker = new FileModifyChecker();
		this.fileModifyChecker.start();
	}

	@Override
	public void stop() throws Exception 
	{
		
	}
}
