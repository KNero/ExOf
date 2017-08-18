package team.balam.exof.module.listener;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.module.Module;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;


public class Listener implements Module
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ServerPort> serverPortList = new LinkedList<>();
	
	private static Listener self = new Listener();
	
	private Listener() {}
	
	public static Listener getInstance()
	{
		return self;
	}
	
	@Override
	public void start() throws Exception
	{
		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.FileName.LISTENER, EnvKey.Listener.PORT);
		portList.forEach(_info -> {
			ServerPort serverPort = new ServerPort(_info);
			this.serverPortList.add(serverPort);
			
			try
			{
				serverPort.open();
				
				if(this.logger.isInfoEnabled())
				{
					this.logger.info("Port is opened. {}", _info.toString());
				}
			}
			catch(Exception e)
			{
				this.logger.error("Can not open port[{}].", _info.getAttribute(EnvKey.Listener.NUMBER), e);
			}
		});
	}

	@Override
	public void stop() throws Exception
	{
		this.serverPortList.forEach(_info -> {
			try
			{
				_info.close();
			}
			catch(Exception e)
			{
				this.logger.error("Can not open port[{}].", _info.getNumber(), e);
			}
			});
	}
}
