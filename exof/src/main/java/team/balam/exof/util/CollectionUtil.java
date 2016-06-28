package team.balam.exof.util;

import java.util.Iterator;
import java.util.function.Consumer;

public class CollectionUtil 
{
	public static <T> void doIterator(Iterable<T> _itr, Consumer<T> _consumer)
	{
		if(_itr != null)
		{
			Iterator<T> iterator = _itr.iterator();
			while(iterator.hasNext())
			{
				_consumer.accept(iterator.next());
			}
		}
	}
}
