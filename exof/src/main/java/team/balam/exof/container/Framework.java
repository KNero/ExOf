package team.balam.exof.container;

import team.balam.exof.Container;
import team.balam.exof.environment.FileModifyChecker;
import team.balam.exof.module.Module;
import team.balam.exof.module.listener.Listener;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.was.JettyModule;



public class Framework implements Container
{
	private Module[] moduleList = new Module[]{new JettyModule(), Listener.getInstance(), ServiceProvider.getInstance()};
	
	@Override
	public String getName() 
	{
		return "Framework";
	}

	@Override
	public void start() throws Exception 
	{
		for(Module m : this.moduleList) m.start();

		new FileModifyChecker().start();
	}

	@Override
	public void stop() throws Exception 
	{
		for(Module m : this.moduleList) m.stop();
	}
}
