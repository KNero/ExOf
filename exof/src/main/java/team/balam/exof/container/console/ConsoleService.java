package team.balam.exof.container.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.service.ServiceProvider;

public class ConsoleService {
	public Object loginWebConsole(Map<String, Object> _param) {
		String id = (String)_param.get("id");
		String password = (String)_param.get("password");

		PortInfo port = (PortInfo)SystemSetting.getInstance().get(EnvKey.FileName.LISTENER, EnvKey.Listener.WEB_CONSOLE);
		String portId = port.getAttribute(EnvKey.Listener.ID);
		String portPw = port.getAttribute(EnvKey.Listener.PASSWORD);
		
		if (portId != null && portId.equals(id)) {
			if (portPw != null && portPw.equals(password)) {
				return WebServlet.LOGIN_SUCCESS;
			}
		}
		
		return Command.NO_DATA_RESPONSE;
	}
	
	public Object getServiceList(Map<String, Object> _param) {
		Map<String, HashMap<String, Object>> result = ServiceProvider.getInstance().getAllServiceInfo();
		
		if (result.size() == 0) {
			return Command.NO_DATA_RESPONSE;
		} else {
			return result;
		}
	}

	public Object getScheduleList(Map<String, Object> _param) {
		List<String> list = SchedulerManager.getInstance().getScheduleList();
		
		if (list.size() == 0) {
			return Command.NO_DATA_RESPONSE;
		} else {
			Map<String, Object> result = new HashMap<>();
			result.put("list", list);

			return result;
		}
	}
}
