package gate.converter;

import gate.error.ConversionException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

public class LocalDateConverterTest
{

	private final String STRING = "01/03/2012";
	private final LocalDate OBJECT = LocalDate.of(2012, Month.MARCH, 1);

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalDate.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
