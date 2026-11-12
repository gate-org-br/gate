package gate.adapter.mapCollector;

import gate.lang.json.JsonObject;
import gate.adapter.jsonConverter.MapJsonConverter;
import gate.lang.constructionStrategy.ConstructionStrategy;
import gate.util.Instance;
import gate.util.Reflection;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class MapCollectorTest
{
	@Test
	void createsSupportedMapTypesAndCopiesEntries()
	{
		Map<Class<?>, Class<?>> types = Map.ofEntries(
				Map.entry(Map.class, LinkedHashMap.class),
				Map.entry(SortedMap.class, TreeMap.class),
				Map.entry(NavigableMap.class, TreeMap.class),
				Map.entry(ConcurrentMap.class, ConcurrentHashMap.class),
				Map.entry(ConcurrentNavigableMap.class, ConcurrentSkipListMap.class),
				Map.entry(HashMap.class, HashMap.class),
				Map.entry(LinkedHashMap.class, LinkedHashMap.class),
				Map.entry(TreeMap.class, TreeMap.class),
				Map.entry(ConcurrentHashMap.class, ConcurrentHashMap.class),
				Map.entry(ConcurrentSkipListMap.class, ConcurrentSkipListMap.class),
				Map.entry(Hashtable.class, Hashtable.class),
				Map.entry(Properties.class, Properties.class),
				Map.entry(CustomMap.class, LinkedHashMap.class));
		types.forEach((type, expected) -> {
			var result = MapCollector.fromMap(type, Map.of("key", "value"));
			assertEquals(expected, result.getClass());
			assertEquals(Map.of("key", "value"), result);
			assertEquals(expected, Instance.create(type).getClass());
		});
	}

	@Test
	void createsEmptyMapsWithoutValues()
	{
		assertEquals(Map.of(), MapCollector.fromMap(SortedMap.class, null));
	}

	@Test
	void resolvesCollectorFromParameterAnnotation() throws Exception
	{
		var parameter = getClass().getDeclaredMethod("customParameter", Map.class).getParameters()[0];
		assertInstanceOf(CustomCollector.class, MapCollector.getCollector(parameter));
	}

	void customParameter(@gate.annotation.MapCollector(CustomCollector.class) Map<?, ?> map) {}

	@Test
	void decodesJsonIntoRequestedMapType()
	{
		var result = new MapJsonConverter().ofJson(
				Reflection.parameterizedType(SortedMap.class, String.class, Integer.class),
				JsonObject.parse("{\"b\":2,\"a\":1}"));
		assertInstanceOf(TreeMap.class, result);
		assertEquals(Map.of("a", 1, "b", 2), result);
	}

	@Test
	void constructsEmptyMapsThroughStrategy()
	{
		assertInstanceOf(TreeMap.class, ConstructionStrategy.newInstance(SortedMap.class));
	}

	@Test
	void honorsCustomCollectorAndPreservesGenericType()
	{
		Type type = Reflection.parameterizedType(AnnotatedMap.class, String.class, Integer.class);
		var result = MapCollector.fromMap(type, Map.of("a", 1));
		assertInstanceOf(AnnotatedMap.class, result);
		assertSame(type, ((AnnotatedMap) result).type);
		assertEquals(Map.of("a", 1), result);
	}

	public static class CustomMap extends LinkedHashMap<Object, Object> {}

	@gate.annotation.MapCollector(CustomCollector.class)
	public static class AnnotatedMap extends LinkedHashMap<Object, Object>
	{
		Type type;
	}

	public static class CustomCollector implements MapCollector
	{
		public Map<?, ?> ofMap(Type type, Map<?, ?> values)
		{
			var result = new AnnotatedMap();
			result.type = type;
			result.putAll(values);
			return result;
		}
	}
}
