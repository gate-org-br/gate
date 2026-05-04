package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class BigDecimalConverterTest extends AbstractSimpleConverterTest<BigDecimal>
{
	@Override protected Class<BigDecimal> getType() {return BigDecimal.class;}

	@Override protected BigDecimal getValue() {return new BigDecimal("123.45");}

	@Override protected String getString() {return "123,45";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("123.45", Converter.toISOString(getValue()));
	}

	@Test
	public void testShouldConvertLocalizedString()
	{
		Assertions.assertEquals(getValue(), Converter.fromString(BigDecimal.class, getString()));
	}
}
