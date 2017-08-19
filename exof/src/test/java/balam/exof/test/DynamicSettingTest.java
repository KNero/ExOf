package balam.exof.test;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import team.balam.exof.environment.DynamicSetting;
import team.balam.exof.environment.vo.DynamicSettingVo;

import java.io.File;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DynamicSettingTest {
	@Test
	public void test01_put() throws Exception {
		File dbFile = new File("./env/env.db");
		dbFile.delete();
		
		for (int i = 0; i < 10; ++i) {
			if (i == 5) {
				DynamicSetting.getInstance().put(new DynamicSettingVo("name" + i, "value" + i, null));
			} else {
				DynamicSetting.getInstance().put(new DynamicSettingVo("name" + i, "value" + i, "des" + i));
			}
		}
	}
	
	@Test
	public void test02_get() throws Exception {
		DynamicSettingVo vo = DynamicSetting.getInstance().get("name0");
		Assert.assertEquals("value0", vo.getValue());
		Assert.assertEquals("des0", vo.getDescription());
		
		vo = DynamicSetting.getInstance().get("name9");
		Assert.assertEquals("value9", vo.getValue());
		Assert.assertEquals("des9", vo.getDescription());
	}
	
	@Test
	public void test03_getList() throws Exception {
		List<DynamicSettingVo> list = DynamicSetting.getInstance().getList("name");
		Assert.assertEquals(10, list.size());
		
		for (int i = 0; i < 10; ++i) {
			Assert.assertEquals("name" + i, list.get(i).getName());
			Assert.assertEquals("value" + i, list.get(i).getValue());

			if (i == 5) {
				Assert.assertEquals(null, list.get(i).getDescription());
			}
		}
	}
	
	@Test 
	public void test04_change() throws Exception {
		DynamicSetting.getInstance().change(new DynamicSettingVo("name3", "value111", "des111"));
		
		DynamicSettingVo vo = DynamicSetting.getInstance().get("name3");
		Assert.assertEquals("value111", vo.getValue());
		Assert.assertEquals("des111", vo.getDescription());
	}
}
