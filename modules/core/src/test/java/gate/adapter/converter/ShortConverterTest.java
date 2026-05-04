package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShortConverterTest extends AbstractSimpleConverterTest<Short>
{
	@Override protected Class<Short> getType() {return Short.class;}

	@Override protected Short getValue() {return 1234;}

	@Override protected String getString() {return "1.234";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("1234", Converter.toISOString(getValue()));
	}
}
