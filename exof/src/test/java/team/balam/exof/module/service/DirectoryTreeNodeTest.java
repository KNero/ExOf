package team.balam.exof.module.service;


import org.junit.Assert;
import org.junit.Test;

public class DirectoryTreeNodeTest {
	@Test
	public void load1() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test", team.balam.exof.test.TestService.class.getName());


		Assert.assertNotNull(root.findServiceDirectory("/test"));
		Assert.assertEquals("schedule", root.findService(new ServiceObject("/test/schedule")).getMethodName());
		Assert.assertEquals("arrayParam", root.findService(new ServiceObject("/test/arrayParam")).getMethodName());
	}

	@Test
	public void load2() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test/1/2", team.balam.exof.test.TestService.class.getName());

		Assert.assertEquals("schedule", root.findService(new ServiceObject("/test/1/2/schedule")).getMethodName());
		Assert.assertNull(root.findService(new ServiceObject("/test/2/arrayParam")));
	}

	@Test
	public void load3() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test/1/2", team.balam.exof.test.TestService.class.getName());

		Assert.assertNotNull(root.findServiceDirectory("/test/1/2"));
	}

	@Test
	public void load4() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test/1/2", team.balam.exof.test.TestService.class.getName());

		Assert.assertNotNull(root.findServiceDirectory("/test/1/2"));
		Assert.assertEquals(1, root.findAllServiceDirectory().size());
	}

	@Test
	public void checkEmpryNameService() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test", team.balam.exof.test.TestService.class.getName());

		Assert.assertNotNull(root.findServiceDirectory("/test"));
		Assert.assertEquals("emptyNameTest", root.findService(new ServiceObject("/test")).getMethodName());
	}

	@Test
	public void checkRootService() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/", team.balam.exof.test.TestService.class.getName());

		Assert.assertEquals("arrayParam", root.findService(new ServiceObject("/arrayParam")).getMethodName());
		Assert.assertEquals("emptyNameTest", root.findService(new ServiceObject("/")).getMethodName());
	}

	@Test
	public void testWildcard1() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();

		DirectoryTreeNode.Builder.append(root, "/wild/{card}", team.balam.exof.test.TestService.class.getName());

		ServiceObject serviceObject = new ServiceObject("/wild/가나다라");
		ServiceWrapper service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("emptyNameTest", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));

		serviceObject = new ServiceObject("/wild/가나다라/arrayParam");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("arrayParam", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));

		DirectoryTreeNode.Builder.append(root, "/wild/1/{card}", team.balam.exof.test.TestService.class.getName());

		serviceObject = new ServiceObject("/wild/1/가나다라");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("emptyNameTest", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));

		serviceObject = new ServiceObject("/wild/1/가나다라/arrayParam");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("arrayParam", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));

		DirectoryTreeNode.Builder.append(root, "/wild/{card}/1/2", team.balam.exof.test.TestService.class.getName());

		serviceObject = new ServiceObject("/wild/가나다라/1/2");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("emptyNameTest", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));

		serviceObject = new ServiceObject("/wild/가나다라/1/2/arrayParam");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("arrayParam", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));

		Assert.assertEquals(3, root.findAllServiceDirectory().size());
	}

	@Test
	public void testWildcard2() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/wild/{card}/{name}", team.balam.exof.test.TestService.class.getName());

		ServiceObject serviceObject = new ServiceObject("/wild/가나다라/smkwon");
		ServiceWrapper service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("emptyNameTest", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));
		Assert.assertEquals("smkwon", serviceObject.getPathVariable("name"));

		serviceObject = new ServiceObject("/wild/가나다라/smkwon/arrayParam");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("arrayParam", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("card"));
	}

	@Test
	public void testWildcard3() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test", team.balam.exof.test.TestService.class.getName());

		ServiceObject serviceObject = new ServiceObject("/test/wild1/가나다라");
		ServiceWrapper service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("testWildcard1", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("1"));

		serviceObject = new ServiceObject("/test/wild2/가나다라");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("testWildcard2", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("2"));

		serviceObject = new ServiceObject("/test/wild1/가나다라/1");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("testWildcard1_1", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("1"));

		serviceObject = new ServiceObject("/test/wild2/가나다라/2");
		service = root.findService(serviceObject);
		Assert.assertNotNull(service);
		Assert.assertEquals("testWildcard2_2", service.getMethodName());
		Assert.assertEquals("가나다라", serviceObject.getPathVariable("2"));
	}

	@Test(expected = ServiceLoadException.class)
	public void testWildcard4() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test", team.balam.exof.test.TestService.class.getName());
		DirectoryTreeNode.Builder.append(root, "/test", team.balam.exof.test.TestService.class.getName());
	}

	@Test
	public void testFindService() throws Exception {
		DirectoryTreeNode root = DirectoryTreeNode.Builder.createRoot();
		DirectoryTreeNode.Builder.append(root, "/test1", team.balam.exof.test.TestService.class.getName());
		DirectoryTreeNode.Builder.append(root, "/test2", team.balam.exof.test.TestService.class.getName());

		Assert.assertNotNull(root.findService(new ServiceObject("/test1")));
		Assert.assertNotNull(root.findService(new ServiceObject("/test2")));
		Assert.assertNull(root.findService(new ServiceObject("/")));

		DirectoryTreeNode.Builder.append(root, "/", team.balam.exof.test.TestService.class.getName());
		Assert.assertNotNull(root.findService(new ServiceObject("/")));
	}
}
