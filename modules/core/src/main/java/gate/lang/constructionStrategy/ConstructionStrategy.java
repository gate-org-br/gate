package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;
import gate.util.Reflection;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Strategy used to construct objects from a set of {@link Attribute} values.
 * <p>
 * Resolution follows a priority order:
 * <ol>
 *     <li><b>Records</b> — always use the canonical constructor;</li>
 *     <li><b>Known collection interfaces</b> ({@code List}, {@code Set}, {@code Map}, etc.) —
 *     created using a default implementation;</li>
 *     <li><b>{@code @Canonical}</b> — a constructor or {@code of(...)} factory explicitly
 *     annotated with {@link Canonical};</li>
 *     <li><b>Builder</b> — a public static {@code builder()} method whose return type has a
 *     {@code build()} method;</li>
 *     <li><b>Single candidate</b> — if exactly one non-deprecated constructor or {@code of(...)}
 *     factory exists with parameters, it is treated as canonical;</li>
 *     <li><b>Attribute match</b> — constructors or factories whose parameters match the provided
 *     attributes, with preference for an exact parameter count;</li>
 *     <li><b>Sealed</b> — if the class is sealed, the non-null attributes determine which
 *     subtype to instantiate; the subtype whose constructor or {@code of(...)} factory parameters
 *     exactly match the non-null attributes is selected — ambiguity or no match throws an
 *     exception;</li>
 *     <li><b>Bean</b> — a public no-argument constructor, with attributes applied via setters.</li>
 * </ol>
 * <p>
 * When multiple attribute-matching candidates exist, the selection is strict: ambiguity throws
 * an exception unless a single exact match (parameter count equal to attribute count) can be found.
 * <p>
 * Missing attributes are handled according to the resolved strategy:
 * <ul>
 *     <li>records, constructors, and static factories receive {@code null} for unmatched parameters;</li>
 *     <li>canonical strategies additionally apply any extra attributes via setters after construction;</li>
 *     <li>builders receive only the attributes that were explicitly provided;</li>
 *     <li>beans receive only the attributes that were explicitly provided.</li>
 * </ul>
 */
public interface ConstructionStrategy
{
	static ConstructionStrategy of(Class<?> type, Set<Attribute> attributes)
	{
		return Cache.INSTANCE.compute(type, attributes, () ->
		{
			if (type.isRecord())
			{
				var types = Arrays.stream(type.getRecordComponents())
						.map(RecordComponent::getType)
						.toArray(Class<?>[]::new);
				try
				{
					return new RecordStrategy(type.getDeclaredConstructor(types));
				} catch (ReflectiveOperationException ex)
				{
					throw new ConstructionException("Unable to get %s canonical constructor"
							.formatted(type.getName()));
				}
			}

			if (type == List.class)
				return new CollectionStrategy(ArrayList::new);
			if (type == Set.class)
				return new CollectionStrategy(HashSet::new);
			if (type == Map.class)
				return new CollectionStrategy(HashMap::new);
			if (type == ConcurrentMap.class)
				return new CollectionStrategy(ConcurrentHashMap::new);
			if (type == Queue.class)
				return new CollectionStrategy(LinkedList::new);
			if (type == Deque.class)
				return new CollectionStrategy(ArrayDeque::new);
			if (type == SortedSet.class)
				return new CollectionStrategy(TreeSet::new);
			if (type == NavigableSet.class)
				return new CollectionStrategy(TreeSet::new);
			if (type == SortedMap.class)
				return new CollectionStrategy(TreeMap::new);
			if (type == NavigableMap.class)
				return new CollectionStrategy(TreeMap::new);
			if (type == BlockingQueue.class)
				return new CollectionStrategy(LinkedBlockingQueue::new);
			if (type == BlockingDeque.class)
				return new CollectionStrategy(LinkedBlockingDeque::new);

			var candidates = getCandidates(type).toList();

			var canonical = candidates.stream()
					.filter(c -> c.isAnnotationPresent(Canonical.class)).toList();
			if (canonical.size() > 1)
				throw new ConstructionException(type, canonical);
			if (!canonical.isEmpty())
				if (canonical.get(0) instanceof Constructor<?> constructor)
					return new CanonicalConstructorStrategy(constructor);
				else if (canonical.get(0) instanceof Method method)
					return new CanonicalFactoryMethodStrategy(method);

			var builderFactory = Arrays.stream(type.getDeclaredMethods())
					.filter(m -> Modifier.isPublic(m.getModifiers()))
					.filter(m -> Modifier.isStatic(m.getModifiers()))
					.filter(m -> !m.isAnnotationPresent(Deprecated.class))
					.filter(m -> m.getName().equals("builder"))
					.findAny().orElse(null);
			if (builderFactory != null)
			{
				var build = Reflection.findMethod(builderFactory.getReturnType(), "build").orElse(null);
				if (build != null)
					return new BuilderStrategy(builderFactory, build);
			}

			if (candidates.size() == 1
			    && candidates.get(0).getParameters().length > 0)
				if (candidates.get(0) instanceof Constructor<?> constructor)
					return new CanonicalConstructorStrategy(constructor);
				else if (candidates.get(0) instanceof Method method)
					return new CanonicalFactoryMethodStrategy(method);

			candidates = candidates.stream()
					.filter(c -> matchesAttributes(attributes, c.getParameters()))
					.toList();
			if (candidates.size() > 1)
			{
				var exact = candidates.stream().filter(c -> c.getParameterCount() == attributes.size()).toList();

				if (exact.size() == 1)
					if (exact.get(0) instanceof Constructor<?> constructor)
						return new ConstructorStrategy(constructor);
					else if (exact.get(0) instanceof Method method)
						return new FactoryMethodStrategy(method);

				throw new ConstructionException(type, attributes, candidates);
			}

			if (!candidates.isEmpty())
				if (candidates.get(0) instanceof Constructor<?> constructor)
					return new ConstructorStrategy(constructor);
				else if (candidates.get(0) instanceof Method method)
					return new FactoryMethodStrategy(method);


			if (type.isSealed())
			{
				Map<Set<String>, Executable> executables = new HashMap<>();
				getSubtypeCandidates(type).forEach(e ->
				{
					var key = Arrays.stream(e.getParameters())
							.map(Parameter::getName)
							.collect(Collectors.toSet());
					var existing = executables.put(key, e);
					if (existing != null)
						throw new ConstructionException(type, Arrays.asList(existing, e));
				});
				if (!executables.isEmpty() &&
				    executables.keySet().stream()
							.flatMap(Set::stream)
							.collect(Collectors.toSet())
							.containsAll(attributes.stream()
									.map(Object::toString)
									.collect(Collectors.toSet())))
					return new SealedConstructorStrategy(executables);
			}

			var defaultConstructor = Arrays.stream(type.getConstructors())
					.filter(c -> c.getParameterCount() == 0)
					.findFirst()
					.orElse(null);
			if (defaultConstructor != null)
				return new BeanStrategy(defaultConstructor);

			throw new ConstructionException(type, attributes);
		});
	}

	private static Stream<Executable> getCandidates(Class<?> type)
	{
		return Stream.concat(Arrays.stream(type.getConstructors())
						.filter(c -> !c.isAnnotationPresent(Deprecated.class)),
				Arrays.stream(type.getDeclaredMethods())
						.filter(m -> Modifier.isPublic(m.getModifiers()))
						.filter(m -> Modifier.isStatic(m.getModifiers()))
						.filter(m -> !m.isAnnotationPresent(Deprecated.class))
						.filter(m -> m.getName().equals("of")));
	}

	private static Stream<Executable> getSubtypeCandidates(Class<?> type)
	{
		if (type.isSealed())
			return Arrays.stream(type.getPermittedSubclasses())
					.flatMap(ConstructionStrategy::getSubtypeCandidates);
		return getCandidates(type);
	}

	private static boolean matchesAttributes(Set<Attribute> attributes, Parameter[] parameters)
	{
		return attributes.stream().allMatch(a -> Arrays.stream(parameters).anyMatch(a::matches));
	}

	static Object newInstance(Class<?> type,
	                          Object value,
	                          Map<Attribute, Object> propertyMap,
	                          TriFunction<Attribute, Object, Object, Object> getValue) throws ReflectiveOperationException
	{
		return of(type, propertyMap.keySet())
				.construct(type, value, propertyMap, getValue);
	}
	static Object newInstance(Class<?> type,
	                          Map<Attribute, Object> attributes)
	{
		return of(type, attributes.keySet())
				.construct(type, attributes);
	}

	static Object newInstance(Class<?> type)
	{
		return newInstance(type, Map.of());
	}

	Object construct(Class<?> type,
	                 Map<Attribute, Object> attributes);
	Object construct(Class<?> type,
	                 Object value,
	                 Map<Attribute, Object> propertyMap,
	                 TriFunction<Attribute, Object, Object, Object> getValue)
			throws ReflectiveOperationException;
}