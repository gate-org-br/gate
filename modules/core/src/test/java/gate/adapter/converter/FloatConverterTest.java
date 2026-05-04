package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FloatConverterTest extends AbstractSimpleConverterTest<Float>
{
	@Override protected Class<Float> getType() {return Float.class;}

	@Override protected Float getValue() {return 1.5F;}

	@Override protected String getString() {return "1,5";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("1.5", Converter.toISOString(getValue()));
	}

	@Test
	public void testShouldConvertLocalizedString()
	{
		Assertions.assertEquals(getValue(), Converter.fromString(Float.class, getString()));
	}
}
