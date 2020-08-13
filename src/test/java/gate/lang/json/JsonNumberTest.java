package gate.lang.json;

import gate.error.ConversionException;
import java.text.ParseException;
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
			assertEquals(JsonNumber.of(30.0), JsonNumber.parse(JsonNumber.of(30.0).toString()));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());

		}
	}

	@Test
	public void testJsonNumberExpNotation() throws ParseException
	{
		try
		{
			assertEquals(JsonNumber.of(310), JsonNumber.parse("3.1e2"));
			assertEquals(JsonNumber.of(300), JsonNumber.parse("3e2"));
			assertEquals(JsonNumber.of(300), JsonNumber.parse("3e+2"));
			assertEquals(JsonNumber.of(0.03), JsonNumber.parse("3e-2"));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}
}
