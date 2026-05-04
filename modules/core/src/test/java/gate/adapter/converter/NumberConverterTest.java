package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class NumberConverterTest extends AbstractSimpleConverterTest<Number>
{
	@Override protected Class<Number> getType() {return Number.class;}

	@Override protected Number getValue() {return new BigDecimal("123.45");}

	@Override protected String getString() {return "123,45";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("123.45",
				Converter.getConverter(Number.class).toISOString(Number.class, getValue()));
	}

	@Test
	public void testShouldConvertLocalizedString()
	{
		Assertions.assertEquals(getValue(), Converter.fromString(Number.class, getString()));
	}
}
