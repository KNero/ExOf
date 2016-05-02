package team.balam.exof.environment;

import java.util.Observable;

public class FileObservable extends Observable
{
	public void updateObservers()
	{
		this.setChanged();
		this.notifyObservers();
	}
}
