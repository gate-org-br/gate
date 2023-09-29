package gate.annotation;

import gate.type.Date;
import gate.type.Sex;
import gate.type.Time;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@Icon("gate.type.Date")
public class IconTest
{

	@Test
	public void testEnum()
	{
		assertEquals("2280", Icon.Extractor.extract(Sex.MALE).get().getCode());
		assertEquals("2281", Icon.Extractor.extract(Sex.FEMALE).get().getCode());

	}

	@Test
	public void testObject()
	{
		assertEquals("2003", Icon.Extractor.extract(Date.now()).get().getCode());
		assertEquals("2167", Icon.Extractor.extract(Time.now()).get().getCode());

	}

	@Test
	public void testClassName()
	{
		assertEquals("2003", Icon.Extractor.extract("gate.type.Date").get().getCode());
		assertEquals("2167", Icon.Extractor.extract("gate.type.Time").get().getCode());
	}

	@Test
	public void testEnumShortcut()
	{
		assertEquals("2280", Icon.Extractor.extract("gate.type.Sex:MALE").get().getCode());
		assertEquals("2281", Icon.Extractor.extract("gate.type.Sex:FEMALE").get().getCode());
	}

	@Test
	@Icon("2003")
	public void testMethodShortcut()
	{
		assertEquals("2003", Icon.Extractor.extract("gate.annotation.IconTest:testMethodShortcut()").get().getCode());
	}

	@Test
	@Icon("gate.util.IconsTest")
	public void testRecursive()
	{
		assertEquals("2003", Icon.Extractor.extract("gate.annotation.IconTest").get().getCode());
	}

	@Test
	@Icon("gate.annotation.IconTest:testRecursive()")
	public void testRecursiveMethodName()
	{
		assertEquals("2003", Icon.Extractor.extract("gate.annotation.IconTest:testRecursiveMethodName()").get().getCode());
	}
}
