package gate.adapter.registrar;

import gate.adapter.mapCollector.*;

import java.util.*;
import java.util.concurrent.*;

public class CoreMapCollectorRegistrar implements MapCollectorRegistrar
{
	@Override
	public Map<Class<?>, MapCollector> entries()
	{
		Map<Class<?>, MapCollector> registry = new HashMap<>();

		registry.put(Map.class, new LinkedHashMapCollector());
		registry.put(LinkedHashMap.class, new LinkedHashMapCollector());
		registry.put(HashMap.class, new HashMapCollector());
		registry.put(SortedMap.class, new SortedMapCollector());
		registry.put(NavigableMap.class, new NavigableMapCollector());
		registry.put(TreeMap.class, new TreeMapCollector());
		registry.put(ConcurrentMap.class, new ConcurrentMapCollector());
		registry.put(ConcurrentHashMap.class, new ConcurrentHashMapCollector());
		registry.put(ConcurrentNavigableMap.class, new ConcurrentNavigableMapCollector());
		registry.put(ConcurrentSkipListMap.class, new ConcurrentSkipListMapCollector());
		registry.put(Hashtable.class, new HashtableCollector());
		registry.put(Properties.class, new PropertiesCollector());

		return registry;
	}
}
