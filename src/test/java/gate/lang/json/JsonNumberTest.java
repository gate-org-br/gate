package gate.lang.json;

import gate.error.ConversionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class JsonNumberTest
{

	@Test
	public void testJsonNumber()
	{
		try
		{
			assertEquals(new JsonNumber(30.0), JsonNumber.parse(JsonNumber.format(new JsonNumber(30.0))));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}
}
