package team.balam.exof.environment.vo;

import team.balam.exof.db.ServiceInfoDao;

import java.util.Map;

/**
 * Loader에서 ServiceProvider로 정보를 전달하기 위해 사용
 * @author kwonsm
 *
 */
public class ServiceDirectoryInfo
{
	public static final ServiceDirectoryInfo NULL_OBJECT = new ServiceDirectoryInfo();
	private boolean isNotNull;

	private Map<String, Object> dbColumn;

	private ServiceDirectoryInfo() {

	}

	public ServiceDirectoryInfo(Map<String, Object> dbColumn) {
		this.isNotNull = true;
		this.dbColumn = dbColumn;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public String getClassName()
	{
		return (String) this.dbColumn.get("class");
	}
	
	public String getPath()
	{
		return (String) this.dbColumn.get("path");
	}
	
	public ServiceVariable getVariable() {
		return ServiceInfoDao.selectServiceVariable(this.getPath());
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Class : ").append(this.getClassName());
		str.append("\nPath : ").append(this.getPath());

		return str.toString();
	}
}
