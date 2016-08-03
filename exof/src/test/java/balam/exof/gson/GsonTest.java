package balam.exof.gson;

import java.lang.reflect.Modifier;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GsonTest
{
	@Test
	public void objectToJson()
	{
		Gson gson = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
		
		System.out.println(gson.toJson(new TestObject()));
	}
	
	@Test
	public void jsonToObject()
	{
		String json = "{\"name\":\"kwonsm\",\"age\":1,\"list\":[\"o1\",2]}";
		
		Gson gson = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.STATIC)
				.excludeFieldsWithoutExposeAnnotation().create();
		
		System.out.println(gson.fromJson(json, TestObject.class));
	}
}
