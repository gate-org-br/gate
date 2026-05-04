package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongConverterTest extends AbstractSimpleConverterTest<Long>
{
	@Override protected Class<Long> getType() {return Long.class;}

	@Override protected Long getValue() {return 1234L;}

	@Override protected String getString() {return "1.234";}

	@Test
	public void testShouldConvertToExpectedISOString()
	{
		Assertions.assertEquals("1234", Converter.toISOString(getValue()));
	}
}
