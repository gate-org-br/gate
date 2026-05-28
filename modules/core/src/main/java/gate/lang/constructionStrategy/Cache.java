package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class Cache
{
	private Cache() {}
	static final Cache INSTANCE = new Cache();

	private final Map<Class<?>, Map<Set<Attribute>, ConstructionStrategy>> VALUES = new ConcurrentHashMap<>();

	void clear() {VALUES.clear();}
	ConstructionStrategy compute(Class<?> type, Set<Attribute> attributes,
	                             Supplier<ConstructionStrategy> supplier)
	{
		return VALUES.computeIfAbsent(type, e -> new ConcurrentHashMap<>())
				.computeIfAbsent(attributes, e -> supplier.get());
	}
}