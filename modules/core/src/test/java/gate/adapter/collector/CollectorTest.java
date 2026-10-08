package gate.adapter.collector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.*;

public class CollectorTest
{
	enum Option {A, B}

	@gate.annotation.Collector(CustomCollector.class)
	static class CustomCollection extends ArrayList<String> {}

	public static class CustomCollector implements Collector
	{
		@Override
		public Object ofArray(Type type, Object[] array)
		{
			return "custom";
		}
	}

	private List<String> list;
	private Set<String> set;
	private SortedSet<String> sortedSet;
	private EnumSet<Option> enumSet;

	@Test
	public void shouldCollectList()
	{
		Object result = Collector.fromArray(typeOf("list"), new String[]{"a", "b"});

		Assertions.assertInstanceOf(ArrayList.class, result);
		Assertions.assertEquals(List.of("a", "b"), result);
	}

	@Test
	public void shouldCollectSet()
	{
		Object result = Collector.fromArray(typeOf("set"), new String[]{"a", "b", "a"});

		Assertions.assertInstanceOf(LinkedHashSet.class, result);
		Assertions.assertEquals(new LinkedHashSet<>(List.of("a", "b")), result);
	}

	@Test
	public void shouldCollectSortedSet()
	{
		Object result = Collector.fromArray(typeOf("sortedSet"), new String[]{"b", "a"});

		Assertions.assertInstanceOf(TreeSet.class, result);
		Assertions.assertEquals(new TreeSet<>(List.of("a", "b")), result);
	}

	@Test
	public void shouldCollectEnumSet()
	{
		Object result = Collector.fromArray(typeOf("enumSet"), new Option[]{Option.A, Option.B});

		Assertions.assertInstanceOf(EnumSet.class, result);
		Assertions.assertEquals(EnumSet.of(Option.A, Option.B), result);
	}

	@Test
	public void shouldUseCollectorAnnotation()
	{
		Object result = Collector.fromArray(CustomCollection.class, new String[]{"a", "b"});

		Assertions.assertEquals("custom", result);
	}

	private Type typeOf(String field)
	{
		try
		{
			return getClass().getDeclaredField(field).getGenericType();
		} catch (NoSuchFieldException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
