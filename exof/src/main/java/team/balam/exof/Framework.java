package team.balam.exof;

import team.balam.exof.environment.FileModifyChecker;
import team.balam.exof.listener.Listener;
import team.balam.exof.service.ServiceProvider;
import team.balam.exof.was.JettyModule;



public class Framework implements Container
{
	private Module[] moduleList = new Module[]{Listener.getInstance(), ServiceProvider.getInstance(), new JettyModule()};
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
