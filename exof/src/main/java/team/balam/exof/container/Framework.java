package team.balam.exof.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.Container;
import team.balam.exof.module.Module;
import team.balam.exof.module.deploy.Deploy;
import team.balam.exof.module.listener.Listener;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.was.JettyModule;
import team.balam.util.sqlite.connection.PoolManager;


public class Framework implements Container
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Framework.class);
	private Module[] moduleList = new Module[]{new JettyModule(),
												new Deploy(),
												Listener.getInstance(),
												ServiceProvider.getInstance()};
	
	@Override
	public String getName() 
	{
		return "Framework";
	}

	@Override
	public void start() throws Exception 
	{
		for(Module m : this.moduleList) {
			try {
				m.start();
			} catch (Exception e) {
				LOGGER.error("Fail to start module.", e);
			}
		}
	}

	@Override
	public void stop() throws Exception 
	{
		for(Module m : this.moduleList) {
			try {
				m.stop();
			} catch (Exception e) {
				LOGGER.error("Fail to stop module.", e);
			}
		}

		PoolManager.destroyPool();
	}
}
