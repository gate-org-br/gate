package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleConverterTest extends AbstractSimpleConverterTest<Double>
{
	@Override protected Class<Double> getType() {return Double.class;}

	@Override protected Double getValue() {return 1.5D;}

	@Override protected String getString() {return "1,5";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("1.5", Converter.toISOString(getValue()));
	}

	@Test
	public void testShouldConvertLocalizedString()
	{
		Assertions.assertEquals(getValue(), Converter.fromString(Double.class, getString()));
	}
}
