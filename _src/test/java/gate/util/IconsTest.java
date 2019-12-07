package gate.util;

import gate.annotation.Icon;
import org.junit.Assert;
import org.junit.Test;

@Icon("gate.type.Date")
public class IconsTest
{

	@Test
	public void testCode()
	{
		Assert.assertEquals("2003", Icons.getInstance().get("2003").get().getCode());
		Assert.assertEquals("1001", Icons.getInstance().get("1001").get().getCode());
		Assert.assertEquals("1000", Icons.getInstance().get("1000").get().getCode());
	}

	@Test
	public void testName()
	{
		Assert.assertEquals("select", Icons.getInstance().get("select").get().getName());
		Assert.assertEquals("insert", Icons.getInstance().get("insert").get().getName());
		Assert.assertEquals("update", Icons.getInstance().get("update").get().getName());
	}

}
