package gate.util;

import gate.annotation.Icon;
import gate.type.Date;
import gate.type.Sex;
import gate.type.Time;
import org.junit.Assert;
import org.junit.Test;

@Icon("gate.type.Date")
public class IconsTest
{

	@Test
	public void testCode()
	{
		Assert.assertEquals("2003",
			Icons.getInstance().get("2003").getCode());
		Assert.assertEquals("1001",
			Icons.getInstance().get("1001").getCode());
		Assert.assertEquals("1000",
			Icons.getInstance().get("1000").getCode());
	}

	@Test
	public void testName()
	{
		Assert.assertEquals("select",
			Icons.getInstance().get("select").getName());
		Assert.assertEquals("insert",
			Icons.getInstance().get("insert").getName());
		Assert.assertEquals("update",
			Icons.getInstance().get("update").getName());
	}

	@Test
	public void testEnum()
	{
		Assert.assertEquals("2280",
			Icons.getInstance().get(Sex.MALE).getCode());
		Assert.assertEquals("2281",
			Icons.getInstance().get(Sex.FEMALE).getCode());

	}

	@Test
	public void testObject()
	{
		Assert.assertEquals("2003",
			Icons.getInstance().get(new Date()).getCode());
		Assert.assertEquals("2167",
			Icons.getInstance().get(new Time()).getCode());

	}

	@Test
	public void testClassName()
	{
		Assert.assertEquals("2003",
			Icons.getInstance().get("gate.type.Date").getCode());
		Assert.assertEquals("2167",
			Icons.getInstance().get("gate.type.Time").getCode());
	}

	@Test
	public void testEnumShortcut()
	{
		Assert.assertEquals("2280",
			Icons.getInstance().get("gate.type.Sex:MALE").getCode());
		Assert.assertEquals("2281",
			Icons.getInstance().get("gate.type.Sex:FEMALE").getCode());
	}

	@Test
	@Icon("2003")
	public void testMethodShortcut()
	{
		Assert.assertEquals("2003",
			Icons.getInstance().get("gate.util.IconsTest:testMethodShortcut()").getCode());
	}

	@Test
	@Icon("gate.util.IconsTest")
	public void testRecursive()
	{
		Assert.assertEquals("2003",
			Icons.getInstance().get("gate.util.IconsTest").getCode());
	}

	@Test
	@Icon("gate.util.IconsTest:testRecursive()")
	public void testRecursiveMethodName()
	{
		Assert.assertEquals("2003",
			Icons.getInstance().get("gate.util.IconsTest:testRecursiveMethodName()").getCode());
	}
}
