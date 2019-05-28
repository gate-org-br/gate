package gate.converter.custom;

import gate.converter.*;
import gate.error.ConversionException;
import gate.type.LocalDateInterval;
import java.time.LocalDate;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

public class LocalDateIntervalConverterTest
{

	private final String STRING = "01/03/2012 - 05/06/2013";
	private final LocalDateInterval OBJECT = LocalDateInterval.of(LocalDate.of(2012, Month.MARCH, 1),
		LocalDate.of(2013, Month.JUNE, 5));

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalDateInterval.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
