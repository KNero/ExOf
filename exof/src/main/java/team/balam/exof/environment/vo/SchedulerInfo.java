package team.balam.exof.environment.vo;

import team.balam.exof.Constant;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * ServiceLoader에서 SchedulerManager로 
 * 스케쥴에 대한 정보를 넘기기 위해서 사용한다.
 * @author kwonsm
 *
 */
public class SchedulerInfo 
{
	private Map<String, Object> dbColumn;
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private boolean isUse;
	private boolean isNull;

	public static final SchedulerInfo NULL_OBJECT = new SchedulerInfo();

	private SchedulerInfo() {
		this.isNull = true;
	}

	public SchedulerInfo(Map<String, Object> _dbInfo) {
		this.dbColumn = _dbInfo;
		this.isUse = Constant.YES.equals(this.dbColumn.get("use"));
	}
	
	public String getId()
	{
		return (String) this.dbColumn.get("id");
	}

	public String getServicePath()
	{
		return (String) this.dbColumn.get("service_path");
	}
	
	public String getCronExpression()
	{
		return (String) this.dbColumn.get("cron");
	}

	public boolean isDuplicateExecution()
	{
		return Constant.YES.equals(this.dbColumn.get("duplicate_execution"));
	}

	public AtomicBoolean getIsRunning()
	{
		return isRunning;
	}
	
	public boolean isUse() {
		return this.isUse;
	}

	public void setUse(boolean use) {
		isUse = use;
	}

	public boolean isInitExecution()
	{
		return Constant.YES.equals(this.dbColumn.get("init_execution"));
	}

	public boolean isNull() {
		return this.isNull;
	}
	
	@Override
	public String toString()
	{
		return this.dbColumn.toString();
	}
}
