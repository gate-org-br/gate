package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.EnumSet;

public class EnumSetConverterTest
{
	private enum Option {FIRST, SECOND}

	@SuppressWarnings("unused")
	private EnumSet<Option> options;

	@Test
	public void testShouldConvertToExpectedString()
	{
		var value = EnumSet.of(Option.FIRST, Option.SECOND);
		var string = Converter.toString(value);
		Assertions.assertEquals("[\"FIRST\", \"SECOND\"]", string);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testShouldConvertToStringAndBack() throws NoSuchFieldException
	{
		var expected = EnumSet.of(Option.FIRST, Option.SECOND);
		var string = Converter.toString(expected);
		Type type = EnumSetConverterTest.class.getDeclaredField("options").getGenericType();
		var value = (EnumSet<Option>) Converter.fromString(type, string);
		Assertions.assertEquals(expected, value);
	}
}
