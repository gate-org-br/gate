package gate.adapter.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RegistryTest
{
	interface First {}
	interface Second {}
	static class Ambiguous implements First, Second {}

	static class StringRegistry extends Registry<String>
	{
		StringRegistry(Map<Class<?>, String> defaults)
		{
			super(defaults);
		}

		@Override
		protected String extractor(Class<?> type)
		{
			return null;
		}

		@Override
		protected String fallback(Class<?> type)
		{
			return "fallback";
		}
	}

	@Test
	public void shouldUseRegisteredSuperInterface()
	{
		var registry = new StringRegistry(Map.of(Collection.class, "collection"));

		Assertions.assertEquals("collection", registry.get(ArrayList.class));
	}

	@Test
	public void shouldPreferNearestInterfaceLevel()
	{
		var registry = new StringRegistry(Map.of(
				Collection.class, "collection",
				List.class, "list"));

		Assertions.assertEquals("list", registry.get(ArrayList.class));
	}

	@Test
	public void shouldRejectAmbiguousInterfacesOnSameLevel()
	{
		var registry = new StringRegistry(Map.of(
				First.class, "first",
				Second.class, "second"));

		Assertions.assertThrows(IllegalStateException.class,
				() -> registry.get(Ambiguous.class));
	}
}
