package gate.converter;

import gate.error.ConversionException;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;

public class ISODurationConverterTest
{

	private static final Converter CONVERTER = new ISODurationConverter();

	@Test
	public void testParseDays() throws ConversionException
	{
		Assert.assertEquals(Duration.ofDays(34), CONVERTER.ofString(Duration.class, "34d"));
		Assert.assertEquals(Duration.ofDays(34), CONVERTER.ofString(Duration.class, "34D"));
	}

	@Test
	public void testFormatDays() throws ConversionException
	{
		Assert.assertEquals("34d", CONVERTER.toString(Duration.class, Duration.ofDays(34)));
	}

	@Test
	public void testParseHours() throws ConversionException
	{
		Assert.assertEquals(Duration.ofHours(34), CONVERTER.ofString(Duration.class, "34h"));
		Assert.assertEquals(Duration.ofHours(34), CONVERTER.ofString(Duration.class, "34H"));
	}

	@Test
	public void testFormatHours() throws ConversionException
	{
		Assert.assertEquals("14h", CONVERTER.toString(Duration.class, Duration.ofHours(14)));
		Assert.assertEquals("1d 10h", CONVERTER.toString(Duration.class, Duration.ofHours(34)));
	}

	@Test
	public void testParseMinutes() throws ConversionException
	{
		Assert.assertEquals(Duration.ofMinutes(34), CONVERTER.ofString(Duration.class, "34"));
		Assert.assertEquals(Duration.ofMinutes(34), CONVERTER.ofString(Duration.class, "34m"));
		Assert.assertEquals(Duration.ofMinutes(34), CONVERTER.ofString(Duration.class, "34M"));
	}

	@Test
	public void testFormatMinutes() throws ConversionException
	{
		Assert.assertEquals("1h 15m", CONVERTER.toString(Duration.class, Duration.ofMinutes(75)));
		Assert.assertEquals("34m", CONVERTER.toString(Duration.class, Duration.ofMinutes(34)));
	}

	@Test
	public void testParseSeconds() throws ConversionException
	{
		Assert.assertEquals(Duration.ofSeconds(34), CONVERTER.ofString(Duration.class, "34s"));
		Assert.assertEquals(Duration.ofSeconds(34), CONVERTER.ofString(Duration.class, "34S"));
	}

	@Test
	public void testFormatSeconds() throws ConversionException
	{
		Assert.assertEquals("1m 15s", CONVERTER.toString(Duration.class, Duration.ofSeconds(75)));
		Assert.assertEquals("34s", CONVERTER.toString(Duration.class, Duration.ofSeconds(34)));
	}

	@Test
	public void testParseComplete() throws ConversionException
	{
		Assert.assertEquals(Duration.ofDays(34).plusHours(12).plusMinutes(50).plusSeconds(20), CONVERTER.ofString(Duration.class, "34d 12h 50m 20s"));
	}

	@Test
	public void testFormatComplete() throws ConversionException
	{
		Assert.assertEquals("34d 12h 50m 20s", CONVERTER.toString(Duration.class, Duration.ofDays(34).plusHours(12).plusMinutes(50).plusSeconds(20)));
	}

	@Test
	public void testParseAlternative() throws ConversionException
	{
		Assert.assertEquals(Duration.ofHours(12).plusMinutes(50), CONVERTER.ofString(Duration.class, "12:50"));
		Assert.assertEquals(Duration.ofHours(12).plusMinutes(50).plusSeconds(20), CONVERTER.ofString(Duration.class, "12:50:20"));
	}
}
