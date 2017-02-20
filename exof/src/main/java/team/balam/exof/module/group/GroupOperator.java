package team.balam.exof.module.group;

import java.util.List;

import team.balam.exof.ConstantKey;
import team.balam.exof.Module;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.ServerPort;

public class GroupOperator implements Module
{
	private ServerPort serverPort; 
	
	@Override
	public void start() throws Exception 
	{
		List<PortInfo> portList = SystemSetting.getInstance().getList(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT);
		portList.forEach(_portInfo -> {
			if(ConstantKey.YES.equals(_portInfo.getAttribute(EnvKey.Listener.GROUP)))
			{
				_portInfo.addAttribute("lengthSize", "4");
				_portInfo.setChannelHandler("team.balam.exof.module.listener.handler.codec.LengthFieldByteCodec");
				_portInfo.setMessageTransform("team.balam.exof.module.group.ServiceTransform");
				this.serverPort = new ServerPort(_portInfo);
			}
		});
		
		if(this.serverPort != null)
		{
			this.serverPort.open();
		}
	}

	@Override
	public void stop() throws Exception 
	{
		if(this.serverPort != null)
		{
			this.serverPort.close();
		}
	}
}
