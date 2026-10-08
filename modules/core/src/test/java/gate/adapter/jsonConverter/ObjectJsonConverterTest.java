package gate.adapter.jsonConverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

public class ObjectJsonConverterTest
{
	private final ObjectJsonConverter converter = new ObjectJsonConverter();

	@Test
	public void testShouldConvertToJsonAndBack()
	{
		var expected = new Convertable("parent", new Convertable("child 1"), new Convertable("child 2"));
		var json = converter.toJson(Convertable.class, expected);
		var value = converter.ofJson(Convertable.class, json);
		Assertions.assertEquals(expected, value);
	}

	public static class Convertable
	{
		private final String name;
		private final Convertable[] children;

		public Convertable(String name)
		{
			this(name, new Convertable[0]);

		}

		public Convertable(String name, Convertable... children)
		{
			this.name = name;
			this.children = children;
		}

		public String getName()
		{
			return name;
		}

		public Convertable[] getChildren()
		{
			return children;
		}

		@Override public boolean equals(Object obj)
		{
			return obj instanceof Convertable other
			       && Objects.equals(name, other.name)
			       && Arrays.equals(children, other.children);
		}
	}
}
