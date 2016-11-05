package balam.exof.test.gson;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Test;

import team.balam.exof.container.console.Command;

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
	
	@Test
	public void sendCommand()
	{
		Socket socket = null;
		
		try
		{
			Command serviceList = new Command(Command.Type.SHOW_SERVICE_LIST);
			
			socket = new Socket("127.0.0.1", 3333);
			socket.getOutputStream().write(serviceList.toJson().getBytes());
			
			byte[] buf = new byte[4096];
			socket.getInputStream().read(buf);
			
			System.out.println(new String(buf));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			Assert.fail();
		}
		finally
		{
			if(socket != null)
			{
				try
				{
					socket.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
