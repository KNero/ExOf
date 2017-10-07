package team.balam.exof.module.deploy;

import team.balam.exof.db.ListenerDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.Module;

public class Deploy implements Module {
	@Override
	public void start() throws Exception {
		PortInfo portInfo = ListenerDao.selectSpecialPort(EnvKey.Listener.DEPLOY);
		if (!portInfo.isNull()) {
			int port = portInfo.getAttributeToInt(EnvKey.Listener.NUMBER, -1);
			if (port == -1) {
				throw new NumberFormatException("Deploy port number is empty.");
			}

			ListenerDao.deletePortAttribute(port);

			ListenerDao.insertPortAttribute(port, EnvKey.Listener.NUMBER, String.valueOf(port));
			ListenerDao.insertChildNode(port, EnvKey.Listener.CHANNEL_HANDLER,
					EnvKey.Listener.CLASS, "team.balam.exof.module.listener.handler.codec.HttpServerCodec");
			ListenerDao.insertChildNode(port, EnvKey.Listener.MESSAGE_TRANSFORM,
					EnvKey.Listener.CLASS, "team.balam.exof.module.listener.handler.transform.HttpTransform");

			ServiceInfoDao.insertServiceDirectory("/exof/deploy", "team.balam.exof.module.deploy.AutoDeployService");
		}
	}

	@Override
	public void stop() throws Exception {

	}
}
