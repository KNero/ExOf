package balam.exof.test;

import org.junit.Test;

import team.balam.exof.container.console.client.InfoGetter;

public class ClientTest
{
	@Test
	public void getScheduleList()
	{
		InfoGetter getter = new InfoGetter();
		getter.getScheduleList();
	}
}
