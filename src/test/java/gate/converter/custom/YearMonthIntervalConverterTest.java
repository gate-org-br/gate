package gate.converter.custom;

import gate.converter.*;
import gate.error.ConversionException;
import gate.type.YearMonthInterval;
import java.time.Month;
import java.time.YearMonth;
import org.junit.Assert;
import org.junit.Test;

public class YearMonthIntervalConverterTest
{

	private final String STRING = "03/2012 - 06/2013";
	private final YearMonthInterval OBJECT = YearMonthInterval.of(YearMonth.of(2012, Month.MARCH),
		YearMonth.of(2013, Month.JUNE));

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(YearMonthInterval.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
