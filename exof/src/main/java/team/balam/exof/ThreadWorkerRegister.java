package team.balam.exof;

import java.util.Vector;

/**
 * 여기에 등록된 Thread는 정기적으로 죽었는지 체크하여 ThreadWorker의 createIfStop()을 호출해 준다.
 * @author kwonsm
 *
 */
public class ThreadWorkerRegister
{
	private Vector<ThreadWorker> workerList;
	private static final ThreadWorkerRegister self = new ThreadWorkerRegister();
	
	private ThreadWorkerRegister()
	{
		this.workerList = new Vector<>();
	}
	
	public static ThreadWorkerRegister getInstance()
	{
		return self;
	}
	
	public void add(ThreadWorker _worker)
	{
		if(! this.workerList.contains(_worker))
		{
			this.workerList.add(_worker);
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
				this.workerList.remove(worker);
				this.workerList.add(worker.createIfStop());
			}
		}
	}
}
