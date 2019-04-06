package team.balam.exof.module.service;

import org.junit.Assert;
import org.junit.Test;

public class TreeServiceNodeMakerTest {
    @Test
    public void test_standardizeServiceName() {
        String str = TreeServiceNodeMaker.standardizeServiceName("/a/b/c/");
        Assert.assertEquals("a/b/c", str);

        str = TreeServiceNodeMaker.standardizeServiceName("");
        Assert.assertEquals(0, str.length());

        str = TreeServiceNodeMaker.standardizeServiceName("//");
        Assert.assertEquals(0, str.length());

        str = TreeServiceNodeMaker.standardizeServiceName("/");
        Assert.assertEquals(0, str.length());

        str = TreeServiceNodeMaker.standardizeServiceName("/a");
        Assert.assertEquals("a", str);

        str = TreeServiceNodeMaker.standardizeServiceName("a");
        Assert.assertEquals("a", str);
    }
}
