package balam.exof;

import balam.exof.environment.FileModifyChecker;
import balam.exof.service.ServiceProvider;



public class Framework implements Container
{
	private Module[] moduleList = new Module[]{ServiceProvider.getInstance()};
	private FileModifyChecker fileModifyChecker;
	
	@Override
	public String getName() 
	{
		return "Framework";
	}

	@Override
	public void start() throws Exception 
	{
		for(Module m : this.moduleList) m.start();
		
		this.fileModifyChecker = new FileModifyChecker();
		this.fileModifyChecker.start();
	}

	@Override
	public void stop() throws Exception 
	{
		for(Module m : this.moduleList) m.stop();
	}
}
