package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class ObjectConverterTest
{
	private final ObjectConverter converter = new ObjectConverter();

	@Test
	public void testShouldConvertToStringAndBack()
	{
		var expected = new Convertable("field1", 2);
		var string = converter.toString(Convertable.class, expected);
		var value = (Convertable) converter.ofString(Convertable.class, string);
		Assertions.assertEquals(expected, value);
	}

	public static class Convertable
	{
		private final String field1;
		private final Integer field2;

		public Convertable(String field1, Integer field2)
		{
			this.field1 = field1;
			this.field2 = field2;
		}

		@Override public boolean equals(Object obj)
		{
			return obj instanceof Convertable convertable
			       && Objects.equals(field1, convertable.field1)
			       && Objects.equals(field2, convertable.field2);
		}
	}
}