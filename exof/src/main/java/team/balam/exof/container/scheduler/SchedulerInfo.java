package team.balam.exof.container.scheduler;

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
	private String id;
	private String servicePath;
	private String cronExpression;
	private boolean isDuplicateExecution;
	private boolean isUse;
	private boolean isInitExecution;
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private boolean isNull;

	public static final SchedulerInfo NULL_OBJECT = new SchedulerInfo();

	private SchedulerInfo() {
		this.isNull = true;
	}

	public SchedulerInfo(Map<String, Object> _dbInfo) {
		this.id = (String) _dbInfo.get("id");
		this.servicePath = (String) _dbInfo.get("service_path");
		this.cronExpression = (String) _dbInfo.get("cron");
		this.isDuplicateExecution = Constant.YES.equals(_dbInfo.get("duplicate_execution"));
		this.isUse = Constant.YES.equals(_dbInfo.get("use"));
		this.isInitExecution = Constant.YES.equals(_dbInfo.get("init_execution"));
	}
	
	public String getId()
	{
		return id;
	}

	public String getServicePath()
	{
		return servicePath;
	}
	
	public String getCronExpression()
	{
		return cronExpression;
	}

	public boolean isDuplicateExecution()
	{
		return isDuplicateExecution;
	}

	public AtomicBoolean getIsRunning()
	{
		return isRunning;
	}
	
	public boolean isUse()
	{
		return isUse;
	}
	
	public boolean isInitExecution() 
	{
		return isInitExecution;
	}

	public boolean isNull() {
		return this.isNull;
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("ID:").append(this.id);
		str.append(", ServicePath:").append(this.servicePath);
		str.append(", Cron:").append(this.cronExpression);
		str.append(", DuplicateExecution:").append(this.isDuplicateExecution);
		str.append(", Use:").append(this.isUse);
		str.append(", InitExecution:").append(this.isInitExecution);
		
		return str.toString();
	}
}
