package gate.adapter.converter;

import gate.type.Percentage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class PercentageConverterTest
{
	private Locale locale;

	@BeforeEach
	public void setUp()
	{
		locale = Locale.getDefault();
		Locale.setDefault(Locale.US);
	}

	@AfterEach
	public void tearDown()
	{
		Locale.setDefault(locale);
	}

	@Test
	public void testShouldConvertToExpectedString()
	{
		var value = Percentage.ZERO;
		var string = Converter.toString(value);
		Assertions.assertEquals("0.00", string);
	}

	@Test
	public void testShouldConvertToStringAndBack()
	{
		var expected = Percentage.ZERO;
		var string = Converter.toString(expected);
		var value = Converter.fromString(Percentage.class, string);
		Assertions.assertEquals(string, Converter.toString(value));
	}
}
