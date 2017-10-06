package balam.exof.test.environment;

import org.junit.Assert;
import org.junit.Test;
import team.balam.exof.ExternalClassLoader;

import java.lang.reflect.Method;

public class LibraryLoaderTest {
	@Test
	public void test_loadLib() throws Exception {
		ExternalClassLoader.load("./lib/ext1");

		Class<?> clazz = ExternalClassLoader.loadClass("team.balam.test.LoadTest");
		Method m = clazz.getMethod("test");
		int result = (Integer) m.invoke(clazz);
		Assert.assertEquals(1, result);
	}

	@Test
	public void test_reloadLib() throws Exception {
		ExternalClassLoader.load("./lib/ext1");

		Class<?> clazz = ExternalClassLoader.loadClass("team.balam.test.LoadTest");
		Method m = clazz.getMethod("test");
		int result = (Integer) m.invoke(clazz);
		Assert.assertEquals(1, result);

		ExternalClassLoader.load("./lib/ext2");

		clazz = ExternalClassLoader.loadClass("team.balam.test.LoadTest");
		m = clazz.getMethod("test");
		result = (Integer) m.invoke(clazz);
		Assert.assertEquals(2, result);
	}
}
