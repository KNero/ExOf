package team.balam.exof.container.scheduler;

import java.util.Date;
import java.util.UUID;

import org.quartz.CronExpression;
import org.quartz.impl.triggers.CronTriggerImpl;

/**
 * 스케쥴러의 실행 시간이 지연될 경우 다음 스케쥴이 
 * 호출될 때 그전에 호출되지 못한 모든 트리거를 실행한다.
 * 그 부분을 방지하기 위해서 nextFireTime을 업데이트 해준다.
 * @author kwonsm
 *
 */
public class PauseAwareCronTrigger extends CronTriggerImpl
{
	private static final long serialVersionUID = -2609414523458705574L;
	
	public PauseAwareCronTrigger(CronExpression _ce)
	{
		super();
		
		this.setCronExpression(_ce);
		this.setName(UUID.randomUUID().toString());
	}
	
	@Override
	public Date getNextFireTime()
	{
		Date nextFireTime = super.getNextFireTime();
		if(nextFireTime.getTime() < System.currentTimeMillis())
		{
			// next fire time after now
			nextFireTime = super.getFireTimeAfter(null);
			super.setNextFireTime(nextFireTime);
		}
		
		return nextFireTime;
	}
}
