package gate.io;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class URLTest
{

	@Test
	public void testParse() throws ConversionException
	{
		assertEquals("http://www.test.org?parameter=value",
			URL.parse("http://www.test.org?parameter=template")
				.setParameter("parameter", "value").toString());
	}

	@Test
	public void testURL() throws ConversionException
	{
		assertEquals("Gate?MODULE=Module&SCREEN=Screen&ACTION=Action&form.id=4",
			new URL("Gate")
				.setModule("Module")
				.setScreen("Screen")
				.setAction("Action")
				.setParameter("form.id", 4).toString());
	}
}
