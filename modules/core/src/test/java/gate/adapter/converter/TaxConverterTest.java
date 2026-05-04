package gate.adapter.converter;

import gate.type.Tax;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;

public class TaxConverterTest
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
		var value = Tax.valueOf(new BigDecimal("1.5"));
		var string = Converter.toString(value);
		Assertions.assertEquals("1.500000", string);
	}

	@Test
	public void testShouldConvertToStringAndBack()
	{
		var expected = Tax.valueOf(new BigDecimal("1.5"));
		var string = Converter.toString(expected);
		var value = Converter.fromString(Tax.class, string);
		Assertions.assertEquals(string, Converter.toString(value));
	}
}
