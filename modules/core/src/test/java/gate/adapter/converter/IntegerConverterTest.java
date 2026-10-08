package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerConverterTest extends AbstractSimpleConverterTest<Integer>
{
	@Override protected Class<Integer> getType() {return Integer.class;}

	@Override protected Integer getValue() {return 1234;}

	@Override protected String getString() {return "1.234";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("1234", Converter.toISOString(getValue()));
	}

	@Test
	public void testShouldConvertLocalizedString()
	{
		Assertions.assertEquals(getValue(), Converter.fromString(Integer.class, getString()));
	}
}
