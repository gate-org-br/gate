package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
