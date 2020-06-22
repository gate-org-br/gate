package gate.lang.json;

import gate.error.ConversionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class JsonBooleanTest
{

	@Test
	public void testJsonBoolean()
	{
		try
		{
			assertEquals(JsonBoolean.TRUE, JsonBoolean.parse(JsonBoolean.TRUE.toString()));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

}
