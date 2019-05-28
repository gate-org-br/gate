package gate.converter;

import gate.error.ConversionException;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

public class LocalDateTimeConverterTest
{

	private final LocalDateTime OBJECT = LocalDateTime.of(2012, Month.MARCH, 1, 1, 23);
	private static final String STRING = "01/03/2012 01:23";

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalDateTime.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
