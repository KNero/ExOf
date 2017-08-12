package team.balam.exof.container.console;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.Constant;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.environment.DynamicSetting;
import team.balam.exof.environment.DynamicSettingVo;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.SystemSetting;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.Service;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ConsoleService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Object loginAdminConsole(Map<String, Object> _param) {
		String id = (String)_param.get("id");
		String password = (String)_param.get("password");

		PortInfo port = (PortInfo)SystemSetting.getInstance().get(EnvKey.FileName.LISTENER, EnvKey.Listener.ADMIN_CONSOLE);
		String portId = port.getAttribute(EnvKey.Listener.ID);
		String portPw = port.getAttribute(EnvKey.Listener.PASSWORD);
		
		if (portId != null && portId.equals(id)) {
			if (portPw != null && portPw.equals(password)) {
				HttpServletRequest httpReq = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
				httpReq.getSession().setAttribute(WebServlet.LOGIN_SUCCESS, Constant.YES);

				return "main.html";
			}
		}
		
		return "index.html";
	}

	public Object getServiceList(Map<String, Object> _param) {
		String findServicePath = (String) _param.get(Command.Key.SERVICE_PATH);

		Map<String, HashMap<String, Object>> result = ServiceProvider.getInstance().getAllServiceInfo();

		if (!StringUtil.isNullOrEmpty(findServicePath)) {
			result.forEach((_key, _value) -> _value.keySet().forEach(_valueKey -> {
				if(!Command.Key.CLASS.equals(_valueKey) && !_valueKey.endsWith(EnvKey.Service.SERVICE_VARIABLE)) {
					String servicePath = _key + "/" + _valueKey;
					if (!servicePath.contains(findServicePath)) {
						_value.put(_valueKey, null);
					}
				}
			}));
		}

		if (result.size() == 0) {
			return Command.NO_DATA_RESPONSE;
		} else {
			return result;
		}
	}

	public Object getScheduleList(Map<String, Object> _param) {
		List<String> resultList = new ArrayList<>();
		List<String> list = SchedulerManager.getInstance().getScheduleList();

		String id = (String) _param.get(Command.Key.ID);
		if (!StringUtil.isNullOrEmpty(id)) {
			for (String info : list) {
				if (info.contains(id)) {
					resultList.add(info);
				}
			}
		} else {
			resultList.addAll(list);
		}

		if (resultList.size() == 0) {
			return Command.NO_DATA_RESPONSE;
		} else {
			Map<String, Object> result = new HashMap<>();
			result.put("list", resultList);

			return result;
		}
	}

	public Object getDynamicSettingList(Map<String, Object> _param) {
		String settingName = (String) _param.get(Command.Key.NAME);
		
		if (settingName == null) {
			settingName = "";
		} 
		
		return DynamicSetting.getInstance().getList(settingName);
	}

	public Object updateDynamicSetting(Map<String, Object> _param) {
		String name = (String) _param.get(Command.Key.NAME);
		String value = (String) _param.get(Command.Key.VALUE);
		String des = (String) _param.get(Command.Key.DESCRIPTION);

		try {
			DynamicSettingVo vo = DynamicSetting.getInstance().get(name);
			if (vo.isValid()) {
				DynamicSetting.getInstance().change(new DynamicSettingVo(name, value, des));
				return Command.SUCCESS_RESPONSE;
			} else {
				return Command.makeSimpleResult("Dynamic setting is not exist. name=" + name);
			}
		} catch (Exception e) {
			this.logger.error("DynamicSetting UPDATE error.", e);
			return Command.makeSimpleResult(e.getMessage());
		}
	}

	public String removeDynamicSetting(Map<String, Object> _param) {
		String name = (String) _param.get(Command.Key.NAME);
		
		try {
			DynamicSettingVo vo = DynamicSetting.getInstance().get(name);
			if (vo.isValid()) {
				DynamicSetting.getInstance().remove(name);
				return Command.SUCCESS_RESPONSE;
			} else {
				return Command.makeSimpleResult("Dynamic setting is not exist. name=" + name);
			}
		} catch (Exception e) {
			this.logger.error("DynamicSetting DELETE error.", e);
			return Command.makeSimpleResult(e.getMessage());
		}
	}

	public String addDynamicSetting(Map<String, Object> _param) {
		String name = (String) _param.get(Command.Key.NAME);
		String value = (String) _param.get(Command.Key.VALUE);
		String des = (String) _param.get(Command.Key.DESCRIPTION);
		
		try {
			DynamicSettingVo vo = DynamicSetting.getInstance().get(name);
			if (!vo.isValid()) {
				DynamicSetting.getInstance().put(new DynamicSettingVo(name, value, des));
				return Command.SUCCESS_RESPONSE;
			} else {
				return Command.makeSimpleResult("Dynamic setting is exist already. name=" + name);
			}
		} catch (Exception e) {
			this.logger.error("DynamicSetting INSERT error.", e);
			return Command.makeSimpleResult(e.getMessage());
		}
	}

	public Object setServiceVariableValue(Map<String, Object> _parameter) {
		String servicePath = (String) _parameter.get(Command.Key.SERVICE_PATH);
		String variableName = (String) _parameter.get(Command.Key.VARIABLE_NAME);
		String variableValue = (String) _parameter.get(Command.Key.VARIABLE_VALUE);

		try {
			ServiceProvider.lookup(servicePath);
		} catch (Exception e) {
			return Command.makeSimpleResult("error : " + e.getMessage());
		}

		try {
			String[] pathArray = servicePath.split("/");
			String serviceName = pathArray[pathArray.length - 1];
			String serviceDirPath = servicePath.split("/" + serviceName)[0];

			List<ServiceDirectoryInfo> serviceList = SystemSetting.getInstance().getList(EnvKey.FileName.SERVICE, EnvKey.Service.SERVICES);
			for (ServiceDirectoryInfo service : serviceList) {
				if (service.getPath().equals(serviceDirPath)) {
					ServiceVariable variable = service.getVariable(serviceName);

					synchronized (variable) {
						this._changeVariable(variable, variableName, variableValue);
					}

					break;
				}
			}

			Service service = ServiceProvider.lookup(servicePath);
			return service.getServiceVariable(variableName);
		} catch (Exception e) {
			this.logger.error("Service variable SET error.", e);
			return Command.makeSimpleResult(e.getMessage());
		}
	}

	/**
	 * ServiceVariable은 내부 적으로 모두 List 로 관리되는데
	 * 하나였던 variable 에 새로운 variable 이 들어갈 경우 List 로 인식된다.
	 * 그러므로 처음 하나를 삭제해서 String 으로 인식되도록 해줘야 한다
	 * @param _serviceVariable ServiceDirectory 의 모든 variable 을 관리하는 객체
	 * @param _variableName serviceVariable name
	 */
	@SuppressWarnings("unchecked")
	private void _changeVariable(ServiceVariable _serviceVariable, String _variableName, String _variableValue) {
		if (_serviceVariable.get(_variableName) instanceof String) {
			_serviceVariable.put(_variableName, _variableValue);

			List<String> variableList = (List<String>) _serviceVariable.get(_variableName);
			variableList.remove(0);
		} else {
			_serviceVariable.put(_variableName, _variableValue);
		}
	}

	public Object setSchedulerOnOff(Map<String, Object> _parameter) {
		String id = (String) _parameter.get(Command.Key.ID);
		String value = (String) _parameter.get(Command.Key.VALUE);

		List<SchedulerInfo> infoList = SystemSetting.getInstance().getList(EnvKey.FileName.SERVICE, EnvKey.Service.SCHEDULER);
		for (SchedulerInfo info : infoList) {
			if (info.getId().equals(id)) {
//				info.setUse(Boolean.parseBoolean(value)); <------ db 사용으로 교체 시 여기 수정
				SchedulerManager.getInstance().update(null, null);

				Map<String, Object> param = new HashMap<>();
				param.put(Command.Key.NAME, id);
				return this.getScheduleList(param);
			}
		}

		return Command.makeSimpleResult("Write the id exactly");
	}
}
