package balam.exof.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class TestObject 
{
	@Expose
	private String name;
	
	@Expose
	private int age;
	
	@Expose
	private List<Object> list = new ArrayList<>();
	
	public TestObject()
	{
		this.name = "kwonsm";
		this.age = 1;
		this.list.add("o1");
		this.list.add(2);
	}
	
	@Override
	public String toString() 
	{
		return this.name + "/" + this.age + "/" + this.list.toString();
	}
}
