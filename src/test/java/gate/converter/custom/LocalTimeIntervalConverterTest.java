package gate.converter.custom;

import gate.converter.*;
import gate.error.ConversionException;
import gate.type.LocalTimeInterval;
import java.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

public class LocalTimeIntervalConverterTest
{

	private final String STRING = "12:14 - 18:25";
	private final LocalTimeInterval OBJECT = LocalTimeInterval.of(LocalTime.of(12, 14), LocalTime.of(18, 25));

	@Test
	public void testOfString() throws ConversionException
	{
		Assert.assertEquals(OBJECT, Converter.fromString(LocalTimeInterval.class, STRING));
	}

	@Test
	public void testOfObject() throws ConversionException
	{
		Assert.assertEquals(STRING, Converter.toString(OBJECT));
	}
}
