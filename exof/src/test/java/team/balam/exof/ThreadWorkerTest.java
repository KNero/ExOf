package team.balam.exof;

import org.junit.Assert;
import org.junit.Test;

public class ThreadWorkerTest extends ThreadWorker
{
	private static boolean isCreateNewWorker;
	private int count;
	
	@Override
	public void run()
	{
		while(true)
		{
			++this.count;
			
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean isStop()
	{
		return this.count % 5 == 0;
	}

	@Override
	public ThreadWorker createIfStop()
	{
		ThreadWorkerTest.isCreateNewWorker = true;
		
		System.out.println("create new threadworker.");
		ThreadWorker tw = new ThreadWorkerTest();
		tw.start();
		return tw;
	}
	
	@Test
	public void testThreadWorker()
	{
		ThreadWorker tw = new ThreadWorkerTest();
		ThreadWorkerRegister.getInstance().add(tw);
		
		tw.start();
		
		for(int i = 0; i < 10; ++i)
		{
			ThreadWorkerRegister.getInstance().check();
			
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		Assert.assertEquals(true, ThreadWorkerTest.isCreateNewWorker);
	}
}
