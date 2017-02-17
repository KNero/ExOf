package team.balam.exof;

public abstract class ThreadWorker extends Thread
{
	abstract public boolean isStop();
	
	abstract public ThreadWorker createIfStop();
}
