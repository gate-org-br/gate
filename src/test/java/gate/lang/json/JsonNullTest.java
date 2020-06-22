package gate.lang.json;

import gate.error.ConversionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class JsonNullTest
{

	@Test
	public void testJsonNull()
	{
		try
		{
			assertEquals(JsonNull.INSTANCE, JsonNull.parse("null"));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

}
