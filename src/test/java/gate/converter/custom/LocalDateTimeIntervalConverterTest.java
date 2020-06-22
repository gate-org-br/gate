package gate.converter.custom;

import gate.converter.*;
import gate.error.ConversionException;
import gate.type.LocalDateTimeInterval;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

public class LocalDateTimeIntervalConverterTest
{

	private final String STRING = "01/03/2012 12:14 - 05/06/2013 18:25";
	private final LocalDateTimeInterval OBJECT = LocalDateTimeInterval.of(LocalDateTime.of(2012, Month.MARCH, 1, 12, 14),
		LocalDateTime.of(2013, Month.JUNE, 5, 18, 25));

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalDateTimeInterval.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
