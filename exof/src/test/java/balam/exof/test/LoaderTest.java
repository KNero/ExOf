package balam.exof.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import team.balam.exof.environment.FrameworkLoader;
import team.balam.exof.environment.SystemSetting;

public class LoaderTest
{
	@Test
	public void testGetFrameworkExternal() throws Exception
	{
		FrameworkLoader loader = new FrameworkLoader();
		loader.load("./env");
		
		Map<String, Object> extMap = SystemSetting.getInstance().getExternal();
		Assert.assertEquals("abcde", extMap.get("test"));
	}
}
