package team.balam.exof.container.console;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.Constant;
import team.balam.exof.container.SchedulerManager;
import team.balam.exof.db.ListenerDao;
import team.balam.exof.db.ServiceInfoDao;
import team.balam.exof.environment.DynamicSetting;
import team.balam.exof.environment.vo.DynamicSettingVo;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.SchedulerInfo;
import team.balam.exof.environment.vo.ServiceDirectoryInfo;
import team.balam.exof.environment.vo.ServiceVariable;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.Service;
import team.balam.exof.module.service.ServiceImpl;
import team.balam.exof.module.service.ServiceNotFoundException;
import team.balam.exof.module.service.ServiceProvider;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ConsoleService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public String loginAdminConsole(Map<String, Object> _param) {
		String id = (String) _param.get("id");
		String password = (String) _param.get("password");

		PortInfo consolePort = ListenerDao.selectAdminConsolePort();

		String portId = consolePort.getAttribute(EnvKey.Listener.ID);
		String portPw = consolePort.getAttribute(EnvKey.Listener.PASSWORD);
		
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

		Map<String, HashMap<String, Object>> result = this.getAllServiceInfo();

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

	private Map<String, HashMap<String, Object>> getAllServiceInfo() {
		Map<String, HashMap<String, Object>> serviceList = new HashMap<>();

		List<ServiceDirectoryInfo> directoryInfoList = ServiceInfoDao.selectServiceDirectory();
		directoryInfoList.forEach(directoryInfo -> {
			HashMap<String, Object> serviceMap = new HashMap<>();
			serviceList.put(directoryInfo.getPath(), serviceMap);

			try {
				Class directoryClass = Class.forName(directoryInfo.getClassName());
				Method[] methods = directoryClass.getMethods();

				for (Method method : methods) {
					team.balam.exof.module.service.annotation.Service serviceAnn =
							method.getAnnotation(team.balam.exof.module.service.annotation.Service.class);

					if (serviceAnn != null) {
						String serviceName = method.getName();
						if (!serviceAnn.name().isEmpty()) {
							serviceName = serviceAnn.name();
						}

						ServiceImpl service = (ServiceImpl) ServiceProvider.lookup(directoryInfo.getPath() + "/" + serviceName);

						if (!serviceMap.containsKey(EnvKey.Service.CLASS)) {
							serviceMap.put(EnvKey.Service.CLASS, service.getHost().getClass().getName());
						}

						Map<String, Object> serviceVariableMap = this.makeServiceVariableMap(service);

						serviceMap.put(serviceName, service.getMethod().getName());
						serviceMap.put(serviceName + EnvKey.Service.SERVICE_VARIABLE, serviceVariableMap);
					}
				}
			} catch (ClassNotFoundException e) {
				this.logger.error("class not found : " + directoryInfo.getClassName(), e);
			} catch (ServiceNotFoundException e) {
				this.logger.error("service not found", e);
			}
		});

		return serviceList;
	}

	private Map<String, Object> makeServiceVariableMap(Service _service) {
		Map<String, Object> variables = new HashMap<>();

		for (String key : _service.getServiceVariableKeys()) {
			variables.put(key, _service.getServiceVariable(key));
		}

		return variables;
	}

	public Object getScheduleList(Map<String, Object> _param) {
		List<String> resultList = new ArrayList<>();
		List<String> list = this._getScheduleList();

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
			return resultList;
		}
	}

	private List<String> _getScheduleList()
	{
		ArrayList<String> list = new ArrayList<>();

		List<SchedulerInfo> infoList = ServiceInfoDao.selectScheduler();
		infoList.forEach(info -> {
			try {
				StringBuilder infoStr = new StringBuilder();
				infoStr.append("ID:").append(info.getId()).append(", service path:").append(info.getServicePath());
				infoStr.append(", cron:").append(info.getCronExpression()).append(", use:").append(info.isUse() ? "yes" : "no");
				infoStr.append(", duplicateExecution:").append(info.isDuplicateExecution() ? "yes" : "no");

				list.add(infoStr.toString());
			} catch(Exception e) {
				String error = "Can not get schedule info. ID[" + info.getId() + "]";
				this.logger.error(error, e);
				list.add(error);
			}
		});

		return list;
	}

	public List<DynamicSettingVo> getDynamicSettingList(Map<String, Object> _param) {
		String settingName = (String) _param.get(Command.Key.NAME);
		
		if (settingName == null) {
			settingName = "";
		} 
		
		return DynamicSetting.getInstance().getList(settingName);
	}

	public String updateDynamicSetting(Map<String, Object> _param) {
		String name = (String) _param.get(Command.Key.NAME);
		String value = (String) _param.get(Command.Key.VALUE);
		String des = (String) _param.get(Command.Key.DESCRIPTION);

		try {
			DynamicSettingVo vo = DynamicSetting.getInstance().get(name);
			if (vo.isValid()) {
				DynamicSetting.getInstance().change(new DynamicSettingVo(name, value, des));
				return DynamicSetting.getInstance().get(name).toString();
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
				return DynamicSetting.getInstance().get(name).toString();
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
			String serviceDirPath = this._getServiceDirectoryPath(servicePath);

			ServiceVariable serviceVariable = ServiceInfoDao.selectServiceVariable(serviceDirPath, serviceName);
			if (serviceVariable.get(variableName) == null) {
				return Command.makeSimpleResult("Variable is not exist.");
			}

			if (serviceVariable.get(variableName) instanceof String) {
				ServiceInfoDao.updateServiceVariableValue(serviceDirPath, serviceName, variableName, variableValue);
			} else {
				ServiceInfoDao.deleteServiceVariable(serviceDirPath, serviceName, variableName);

				String[] values = variableValue.split(",");
				for(String value : values) {
					ServiceInfoDao.insertServiceVariable(serviceDirPath, serviceName, variableName, value.trim());
				}
			}

			ServiceProvider.getInstance().update(null, null);

			Service service = ServiceProvider.lookup(servicePath);
			return service.getServiceVariable(variableName);
		} catch (Exception e) {
			this.logger.error("Service variable SET error.", e);
			return Command.makeSimpleResult(e.getMessage());
		}
	}

	private String _getServiceDirectoryPath(String _servicePath) {
		int lastSlash;
		for (lastSlash = _servicePath.length() - 1; lastSlash >= 0; --lastSlash) {
			if (_servicePath.charAt(lastSlash) == '/') {
				break;
			}
		}

		if (lastSlash > 0) {
			return _servicePath.substring(0, lastSlash);
		} else {
			return Constant.EMPTY_STRING;
		}
	}

	public Object setSchedulerOnOff(Map<String, Object> _parameter) {
		String id = (String) _parameter.get(Command.Key.ID);
		String value = (String) _parameter.get(Command.Key.VALUE);

		ServiceInfoDao.updateSchedulerUse(id, value);
		SchedulerManager.getInstance().update(null, id);

		return this.getScheduleList(_parameter);
	}

	public Object setSchedulerCron(Map<String, Object> _parameter) {
		String id = (String) _parameter.get(Command.Key.ID);
		String cron = (String) _parameter.get(Command.Key.CRON);

		SchedulerInfo info = ServiceInfoDao.selectScheduler(id);
		if (info.isNull()) {
			return Command.makeSimpleResult("ID[" + id + "] is not exists.");
		}

		ServiceInfoDao.updateSchedulerCron(id, cron);
		SchedulerManager.getInstance().update(null, id);

		return this.getScheduleList(_parameter);
	}

	public Object getPortInfo(Map<String, Object> _parameter) {
		List<Map<String, Object>> result = new ArrayList<>();

		List<PortInfo> infoList = ListenerDao.selectPortList();
		for (PortInfo portInfo : infoList) {
			Map<String, Object> map = new HashMap<>();
			map.put(Command.Key.PORT, portInfo.getNumber());

			List<Map<String, Object>> attr = ListenerDao.selectPortAttribute(portInfo.getNumber());
			map.put(Command.Key.ATTRIBUTE, attr);

			List<Map<String, Object>> childAttr = ListenerDao.selectChildAttribute(portInfo.getNumber());
			map.put(Command.Key.CHILD_ATTRIBUTE, childAttr);

			result.add(map);
		}

		return result;
	}
}
