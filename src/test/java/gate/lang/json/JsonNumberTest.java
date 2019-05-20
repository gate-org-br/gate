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
			assertEquals(JsonNumber.valueOf(30.0), JsonNumber.parse(JsonNumber.valueOf(30.0).toString()));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}
}
