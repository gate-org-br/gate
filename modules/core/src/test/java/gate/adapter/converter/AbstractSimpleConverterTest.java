package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class AbstractSimpleConverterTest<T>
{
	protected abstract Class<T> getType();

	protected abstract T getValue();

	protected abstract String getString();

	@Test
	public void testShouldConvertToExpectedString()
	{
		Assertions.assertEquals(getString(), Converter.toString(getValue()));
	}

	@Test
	public void testShouldConvertToStringAndBack()
	{
		var value = Converter.fromString(getType(), getString());
		Assertions.assertEquals(getString(), Converter.toString(value));
	}
}
