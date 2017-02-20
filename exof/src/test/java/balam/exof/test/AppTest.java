package balam.exof.test;

import org.junit.Test;

import team.balam.exof.ThreadWorker;
import team.balam.exof.ThreadWorkerRegister;

public class AppTest extends ThreadWorker
{
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
		System.out.println("create new threadworker.");
		ThreadWorker tw = new AppTest();
		tw.start();
		return tw;
	}
	
	@Test
	public void testThreadWorker()
	{
		ThreadWorker tw = new AppTest();
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
	}
}
