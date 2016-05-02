package team.balam.exof.util;

import java.util.List;

public class CircularList<T>
{
	private Content first;
	private Content current;
	
	public CircularList()
	{
		
	}
	
	public CircularList(List<T> _list)
	{
		this.set(_list);
	}
	
	public void set(List<T> _list)
	{
		if(_list == null || _list.size() == 0) return;
		
		this.first = new Content(_list.get(0));
		
		Content next = this.first;
		
		int listSize = _list.size();
		for(int i = 0; i < listSize; ++i)
		{
			if(i < listSize - 1)
			{
				next.next = new Content(_list.get(i + 1));
				next = next.next;
			}
			else
			{
				next.isTail = true;
				next.next = this.first;
			}
		}
		
		this.current = this.first;
	}
	
	public T next()
	{
		if(this.current == null) return null;
		
		T t = this.current.t;
		this.current = this.current.next;
		
		return t;
	}
	
	public void reset()
	{
		this.current = this.first;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("(");
		
		Content content = this.first;
		while(! content.isTail)
		{
			sb.append(content.toString()).append(", ");
			content = content.next;
		}
		
		sb.append(content.toString()).append(")");
		
		return sb.toString();
	}
	
	private class Content
	{
		private T t;
		private Content next;
		private boolean isTail;
		
		public Content(T _t)
		{
			this.t = _t;
		}
		
		@Override
		public String toString()
		{
			return this.t.toString();
		}
	}
}
