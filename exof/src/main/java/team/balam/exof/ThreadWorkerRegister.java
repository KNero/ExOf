package team.balam.exof;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 여기에 등록된 Thread는 정기적으로 죽었는지 체크하여 ThreadWorker의 createIfStop()을 호출해 준다.
 * @author kwonsm
 *
 */
public class ThreadWorkerRegister
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<ThreadWorker> workerList;
	private static final ThreadWorkerRegister self = new ThreadWorkerRegister();
	
	private ThreadWorkerRegister()
	{
		this.workerList = new CopyOnWriteArrayList<>();
	}
	
	public static ThreadWorkerRegister getInstance()
	{
		return self;
	}
	
	public void add(ThreadWorker _worker)
	{
		if(_worker == null)
		{
			this.logger.error("ThreadWorker is null.", new NullPointerException());
		}
		
		if(! this.workerList.contains(_worker))
		{
			this.workerList.add(_worker);
		}
		else
		{
			this.logger.error("ThreadWorker is already exists. => " + _worker.getName());
		}
	}
	
	public void remove(ThreadWorker _worker)
	{
		this.workerList.remove(_worker);
	}

	public void check() 
	{
		for(ThreadWorker worker : this.workerList)
		{
			if(worker.isStop() || ! worker.isAlive())
			{
				this.logger.error("ThreadWorker[" + worker.getName() +"] is stoppted. Created ThreadWorker.");
				
				this.workerList.remove(worker);
				this.workerList.add(worker.createIfStop());
			}
		}
	}
}
