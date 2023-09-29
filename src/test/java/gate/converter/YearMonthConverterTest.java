package gate.converter;

import gate.error.ConversionException;
import java.time.Month;
import java.time.YearMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class YearMonthConverterTest
{

	private final String STRING = "03/2012";
	private final YearMonth OBJECT = YearMonth.of(2012, Month.MARCH);

	@Test
	public void testOfString() throws ConversionException
	{
		assertEquals(OBJECT, Converter.fromString(YearMonth.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		assertEquals(STRING, Converter.toString(OBJECT));
	}
}
