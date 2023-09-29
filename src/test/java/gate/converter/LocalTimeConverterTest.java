package gate.converter;

import gate.error.ConversionException;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class LocalTimeConverterTest
{

	private static final String STRING = "01:22";
	private final LocalTime OBJECT = LocalTime.of(1, 22);

	@Test
	public void testOfString() throws ConversionException
	{
		assertEquals(OBJECT, Converter.fromString(LocalTime.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		assertEquals(STRING, Converter.toString(OBJECT));
	}
}
