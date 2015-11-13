package balam.exof.scheduler;

public class SchedulerAlreadyExists extends Exception
{
	private static final long serialVersionUID = -5440914206069628857L;

	public SchedulerAlreadyExists(String _id)
	{
		super("SchedulerID[" + _id + "] already exists.");
	}
}
