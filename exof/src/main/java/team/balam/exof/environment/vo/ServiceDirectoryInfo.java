package team.balam.exof.environment.vo;

import team.balam.exof.module.service.ServiceVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Loader에서 ServiceProvider로 정보를 전달하기 위해 사용
 * @author kwonsm
 *
 */
public class ServiceDirectoryInfo
{
	private Map<String, Object> dbColumn;

	public ServiceDirectoryInfo(Map<String, Object> dbColumn) {
		this.dbColumn = dbColumn;
	}
	
	public String getClassName()
	{
		return (String) this.dbColumn.get("class");
	}
	
	public String getPath()
	{
		return (String) this.dbColumn.get("path");
	}
	
	public ServiceVariable getVariable(String _serviceName) {
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Class : ").append(this.getClassName());
		str.append("\nPath : ").append(this.getPath());
		str.append("\n- Variable List");

//		for(String serviceName : this.variableMap.keySet()) {
//			str.append("\n-- Service Name : ").append(serviceName);
//
//			ServiceVariable variable = this.variableMap.get(serviceName);
//			str.append("\n-- Service Variable\n").append(variable.toString());
//		}

		return str.toString();
	}
}