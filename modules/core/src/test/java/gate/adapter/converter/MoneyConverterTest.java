package gate.adapter.converter;

import gate.type.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class MoneyConverterTest
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
		var value = new Money(10);
		var string = Converter.toString(value);
		Assertions.assertEquals("10.00", string);
	}

	@Test
	public void testShouldConvertToStringAndBack()
	{
		var expected = new Money(10);
		var string = Converter.toString(expected);
		var value = Converter.fromString(Money.class, string);
		Assertions.assertEquals(expected, value);
	}
}
