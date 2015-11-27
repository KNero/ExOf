package balam.exof.listener;

import java.util.List;

import balam.exof.Module;
import balam.exof.environment.EnvKey;
import balam.exof.environment.SystemSetting;


public class Listener implements Module
{
	private static Listener self = new Listener();
	
	private Listener() {}
	
	public static Listener getInstance()
	{
		return self;
	}
	
	@Override
	public void start() throws Exception
	{
		List<PortInfo> info = SystemSetting.getInstance().getListAndRemove(EnvKey.PreFix.LISTENER, EnvKey.Listener.PORT);
	}

	@Override
	public void stop() throws Exception
	{
		
	}
}
