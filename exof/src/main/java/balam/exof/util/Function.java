package balam.exof.util;

import java.util.Iterator;

public class Function 
{
	@SuppressWarnings("rawtypes")
	public static void doIterator(Iterable _itr, Callback _callback)
	{
		Iterator iterator = _itr.iterator();
		while(iterator.hasNext())
		{
			_callback.execute(iterator.next());
		}
	}
}
