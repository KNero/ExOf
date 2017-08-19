package team.balam.exof.module.listener;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.balam.exof.db.ListenerDao;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.Module;
import team.balam.exof.environment.EnvKey;


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
	public void start() throws Exception {
		List<PortInfo> portNumberList = ListenerDao.selectPortList();
		portNumberList.forEach(info -> {
			if (info.isSpecial()) {
				return;
			}

			ServerPort serverPort = new ServerPort(info);
			this.serverPortList.add(serverPort);

			try {
				serverPort.open();

				if(this.logger.isInfoEnabled()) {
					this.logger.info("Port is opened. {}", info.toString());
				}
			} catch(Exception e) {
				this.logger.error("Can not open port[{}].", info.getAttribute(EnvKey.Listener.NUMBER), e);
			}
		});
	}

	@Override
	public void stop() throws Exception {
		this.serverPortList.forEach(_info -> {
			try {
				_info.close();
			} catch(Exception e) {
				this.logger.error("Can not open port[{}].", _info.getNumber(), e);
			}
		});
	}
}
