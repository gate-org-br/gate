package gate.converter;

import gate.error.ConversionException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

public class LocalDateConverterTest
{

	private static final String STRING = "01/03/2012";
	private static final String ISO_STRING = "2012-03-01";
	private static final LocalDate OBJECT = LocalDate.of(2012, Month.MARCH, 1);

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalDate.class, STRING));
	}

	@Test
	public void testOfISOString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalDate.class, "2012-03-01"));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}

	@Test
	public void testISOObject() throws ConversionException
	{
		Assert.assertEquals(ISO_STRING, Converter.toISOString(OBJECT));
	}
}
