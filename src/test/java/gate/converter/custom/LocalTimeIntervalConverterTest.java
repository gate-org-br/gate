package gate.converter.custom;

import gate.converter.*;
import gate.error.ConversionException;
import gate.type.LocalTimeInterval;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class LocalTimeIntervalConverterTest
{

	private final String STRING = "12:14 - 18:25";
	private final LocalTimeInterval OBJECT = LocalTimeInterval.of(LocalTime.of(12, 14), LocalTime.of(18, 25));

	@Test
	public void testOfString() throws ConversionException
	{
		assertEquals(OBJECT, Converter.fromString(LocalTimeInterval.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		assertEquals(STRING, Converter.toString(OBJECT));
	}
}
