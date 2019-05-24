package gate.lang.json;

import gate.error.ConversionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class JsonStringTest
{

	@Test
	public void testJsonString()
	{
		try
		{
			assertEquals(JsonString.of("String"), JsonString
				.parse(JsonString.of("String").toString()));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

	@Test
	public void testDoubleQuotedEscapedString()
	{
		try
		{
			assertEquals("Jonh\"s", JsonString.parse("\"Jonh\\\"s\"").toString());
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

	@Test
	public void testSingleQuotedEscapedString()
	{
		try
		{
			assertEquals("Jonh's", JsonString.parse("'Jonh\\'s'").toString());
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}
}
