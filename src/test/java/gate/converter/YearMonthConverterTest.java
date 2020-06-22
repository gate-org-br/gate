package gate.converter;

import gate.error.ConversionException;
import java.time.Month;
import java.time.YearMonth;
import org.junit.Assert;
import org.junit.Test;

public class YearMonthConverterTest
{

	private final String STRING = "03/2012";
	private final YearMonth OBJECT = YearMonth.of(2012, Month.MARCH);

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(YearMonth.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
