package balam.exof.util;

import java.util.Iterator;

public class CollectionUtil 
{
	public static <T> void doIterator(Iterable<T> _itr, Callback<T> _callback)
	{
		if(_itr != null)
		{
			Iterator<T> iterator = _itr.iterator();
			while(iterator.hasNext())
			{
				_callback.execute(iterator.next());
			}
		}
	}
}
