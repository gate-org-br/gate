package gate.annotation;

import gate.type.Date;
import gate.type.Sex;
import gate.type.Time;
import org.junit.Assert;
import org.junit.Test;

@Icon("gate.type.Date")
public class IconTest
{

	@Test
	public void testEnum()
	{
		Assert.assertEquals("2280", Icon.Extractor.extract(Sex.MALE).orElseThrow().getCode());
		Assert.assertEquals("2281", Icon.Extractor.extract(Sex.FEMALE).orElseThrow().getCode());

	}

	@Test
	public void testObject()
	{
		Assert.assertEquals("2003", Icon.Extractor.extract(new Date()).orElseThrow().getCode());
		Assert.assertEquals("2167", Icon.Extractor.extract(new Time()).orElseThrow().getCode());

	}

	@Test
	public void testClassName()
	{
		Assert.assertEquals("2003", Icon.Extractor.extract("gate.type.Date").orElseThrow().getCode());
		Assert.assertEquals("2167", Icon.Extractor.extract("gate.type.Time").orElseThrow().getCode());
	}

	@Test
	public void testEnumShortcut()
	{
		Assert.assertEquals("2280", Icon.Extractor.extract("gate.type.Sex:MALE").orElseThrow().getCode());
		Assert.assertEquals("2281", Icon.Extractor.extract("gate.type.Sex:FEMALE").orElseThrow().getCode());
	}

	@Test
	@Icon("2003")
	public void testMethodShortcut()
	{
		Assert.assertEquals("2003", Icon.Extractor.extract("gate.annotation.IconTest:testMethodShortcut()").orElseThrow().getCode());
	}

	@Test
	@Icon("gate.util.IconsTest")
	public void testRecursive()
	{
		Assert.assertEquals("2003", Icon.Extractor.extract("gate.annotation.IconTest").orElseThrow().getCode());
	}

	@Test
	@Icon("gate.annotation.IconTest:testRecursive()")
	public void testRecursiveMethodName()
	{
		Assert.assertEquals("2003", Icon.Extractor.extract("gate.annotation.IconTest:testRecursiveMethodName()").orElseThrow().getCode());
	}
}
