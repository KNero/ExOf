package team.balam.exof.util;

import org.junit.Assert;
import org.junit.Test;

public class PipelineTest {
	@Test
	public void test1() {
		Pipeline pipeline = new Pipeline();
		pipeline.add(in -> {
			Assert.assertEquals(in, 1);
			return 2;
		}).add(in -> {
			Assert.assertEquals(in, 2);
			return 3;
		}).add(in -> {
			Assert.assertEquals(in, 3);
			return 4;
		});

		int result = (Integer) pipeline.execute(1);
		Assert.assertEquals(result, 4);
	}

	@Test
	public void test2() {
		Pipeline pipeline = new Pipeline();
		pipeline.add(in -> {
			Assert.assertEquals(in, 1);
			return 2;
		}).add(new Pipeline.Pipe() {
			@Override
			public Object execute(Object in) {
				Assert.assertEquals(in, 2);
				return 3;
			}

			@Override
			public boolean isStop() {
				return true;
			}
		}).add(in -> {
			Assert.assertEquals(in, 3);
			return 4;
		});

		int result = (Integer) pipeline.execute(1);
		Assert.assertEquals(result, 3);
	}
}
