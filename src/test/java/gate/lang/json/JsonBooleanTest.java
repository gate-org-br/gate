package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
