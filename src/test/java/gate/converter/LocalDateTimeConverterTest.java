package gate.converter;

import gate.error.ConversionException;
import java.time.LocalDateTime;
import java.time.Month;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class LocalDateTimeConverterTest
{

	private final LocalDateTime OBJECT = LocalDateTime.of(2012, Month.MARCH, 1, 1, 23);
	private static final String STRING = "01/03/2012 01:23";
	private static final String ISO_STRING = "2012-03-01T01:23";

	@Test
	public void testOfString() throws ConversionException
	{
		assertEquals(OBJECT, Converter.fromString(LocalDateTime.class, STRING));
	}

	@Test
	public void testOfISOString() throws ConversionException
	{
		assertEquals(OBJECT, Converter.fromString(LocalDateTime.class, ISO_STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		assertEquals(STRING, Converter.toString(OBJECT));
	}

	@Test
	public void testOfISOObject() throws ConversionException
	{
		assertEquals(ISO_STRING, Converter.toISOString(OBJECT));
	}
}
