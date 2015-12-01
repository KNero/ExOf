package balam.exof.listener;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import balam.exof.Module;
import balam.exof.environment.EnvKey;
import balam.exof.environment.SystemSetting;
import balam.exof.util.CollectionUtil;


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
		List<PortInfo> portList = SystemSetting.getInstance().getListAndRemove(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT);
		CollectionUtil.doIterator(portList, _info -> {
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
				this.logger.error("Can not open port[{}].", _info.getNumber(), e);
			}
		});
	}

	@Override
	public void stop() throws Exception
	{
		CollectionUtil.doIterator(this.serverPortList, _info -> {
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
