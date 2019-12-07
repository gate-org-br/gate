package gate.converter;

import gate.error.ConversionException;
import java.time.Duration;
import java.util.Locale;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DurationConverterTest
{

	@BeforeClass
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void testConvertHHMMSSToString() throws Exception
	{
		String expected = "02:01:30";
		String result = Converter.toString(Duration
			.ofHours(2)
			.plusMinutes(1)
			.plusSeconds(30));
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertMMSSToString() throws Exception
	{
		String expected = "00:01:30";
		String result = Converter.toString(Duration
			.ofMinutes(1)
			.plusSeconds(30));
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertSSToString() throws Exception
	{
		String expected = "00:00:30";
		String result = Converter.toString(Duration
			.ofSeconds(30));
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertHHMMSSToObject() throws Exception
	{
		String string = "02:01:30";
		Duration expected = Duration
			.ofHours(2)
			.plusMinutes(1)
			.plusSeconds(30);

		Duration result = (Duration) Converter.getConverter(Duration.class).ofString(Duration.class, string);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertMMSSToObject() throws Exception
	{
		String string = "02:01";
		Duration expected = Duration
			.ofMinutes(2)
			.plusSeconds(1);

		Duration result = (Duration) Converter.getConverter(Duration.class).ofString(Duration.class, string);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertSSToObject() throws Exception
	{
		String string = "01";
		Duration expected = Duration
			.ofSeconds(1);

		Duration result = (Duration) Converter.getConverter(Duration.class).ofString(Duration.class, string);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertStringWithErrorToObject()
	{
		try
		{
			Converter.getConverter(Duration.class).ofString(Duration.class, "02:01::30");
			Assert.fail();
		} catch (ConversionException ex)
		{
		}

		try
		{
			Converter.getConverter(Duration.class).ofString(Duration.class, "02:01::");
			Assert.fail();
		} catch (ConversionException ex)
		{
		}

		try
		{
			Converter.getConverter(Duration.class).ofString(Duration.class, ":");
			Assert.fail();
		} catch (ConversionException ex)
		{
		}
	}
}
