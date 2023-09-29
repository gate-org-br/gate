package gate.converter;

import gate.error.ConversionException;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DurationConverterTest
{

	private static final Converter CONVERTER = new DurationConverter();

	@Test
	public void testParseDays() throws ConversionException
	{
		assertEquals(Duration.ofDays(34), CONVERTER.ofString(Duration.class, "34d"));
		assertEquals(Duration.ofDays(34), CONVERTER.ofString(Duration.class, "34D"));
	}

	@Test
	public void testFormatDays() throws ConversionException
	{
		assertEquals("34d", CONVERTER.toString(Duration.class, Duration.ofDays(34)));
	}

	@Test
	public void testParseHours() throws ConversionException
	{
		assertEquals(Duration.ofHours(34), CONVERTER.ofString(Duration.class, "34h"));
		assertEquals(Duration.ofHours(34), CONVERTER.ofString(Duration.class, "34H"));
	}

	@Test
	public void testFormatHours() throws ConversionException
	{
		assertEquals("14h", CONVERTER.toString(Duration.class, Duration.ofHours(14)));
		assertEquals("1d 10h", CONVERTER.toString(Duration.class, Duration.ofHours(34)));
	}

	@Test
	public void testParseMinutes() throws ConversionException
	{
		assertEquals(Duration.ofMinutes(34), CONVERTER.ofString(Duration.class, "34"));
		assertEquals(Duration.ofMinutes(34), CONVERTER.ofString(Duration.class, "34m"));
		assertEquals(Duration.ofMinutes(34), CONVERTER.ofString(Duration.class, "34M"));
	}

	@Test
	public void testFormatMinutes() throws ConversionException
	{
		assertEquals("1h 15m", CONVERTER.toString(Duration.class, Duration.ofMinutes(75)));
		assertEquals("34m", CONVERTER.toString(Duration.class, Duration.ofMinutes(34)));
	}

	@Test
	public void testParseSeconds() throws ConversionException
	{
		assertEquals(Duration.ofSeconds(34), CONVERTER.ofString(Duration.class, "34s"));
		assertEquals(Duration.ofSeconds(34), CONVERTER.ofString(Duration.class, "34S"));
	}

	@Test
	public void testFormatSeconds() throws ConversionException
	{
		assertEquals("1m 15s", CONVERTER.toString(Duration.class, Duration.ofSeconds(75)));
		assertEquals("34s", CONVERTER.toString(Duration.class, Duration.ofSeconds(34)));
	}

	@Test
	public void testParseComplete() throws ConversionException
	{
		assertEquals(Duration.ofDays(34).plusHours(12).plusMinutes(50).plusSeconds(20), CONVERTER.ofString(Duration.class, "34d 12h 50m 20s"));
	}

	@Test
	public void testFormatComplete() throws ConversionException
	{
		assertEquals("34d 12h 50m 20s", CONVERTER.toString(Duration.class, Duration.ofDays(34).plusHours(12).plusMinutes(50).plusSeconds(20)));
	}

	@Test
	public void testParseAlternative() throws ConversionException
	{
		assertEquals(Duration.ofHours(12).plusMinutes(50), CONVERTER.ofString(Duration.class, "12:50"));
		assertEquals(Duration.ofHours(12).plusMinutes(50).plusSeconds(20), CONVERTER.ofString(Duration.class, "12:50:20"));
	}

	@Test
	public void testFormatHHMM() throws ConversionException
	{
		assertEquals("01:15", CONVERTER.toText(Duration.class, Duration.ofMinutes(75), "hh:mm"));

		assertEquals("00:01:15:00", CONVERTER.toText(Duration.class, Duration.ofMinutes(75), "dd:hh:mm:ss"));
	}
}
