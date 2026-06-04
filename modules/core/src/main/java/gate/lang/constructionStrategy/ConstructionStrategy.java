package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.annotation.Discriminator;
import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.util.Reflection;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Strategy used to construct objects from a set of {@link Attribute} values.
 * <p>
 * Resolution follows a priority order:
 * <ol>
 *     <li><b>Records</b> — always use the canonical constructor;</li>
 *     <li><b>Known collection interfaces</b> ({@code List}, {@code Set}, {@code Map}, etc.) —
 *     created using a default implementation;</li>
 *     <li><b>Single {@code @Canonical}</b> — a constructor or {@code of(...)} factory explicitly
 *     annotated with {@link Canonical} is selected immediately, before any attribute matching;</li>
 *     <li><b>Builder</b> — a public static {@code builder()} method whose return type has a
 *     {@code build()} method;</li>
 *     <li><b>Single compatible candidate</b> — if exactly one constructor or {@code of(...)}
 *     factory can consume the provided attributes, it is treated as canonical;</li>
 *     <li><b>Single exact match</b> — if multiple candidates are compatible, but exactly one has
 *     the same parameter count as the provided attribute count, it is selected;</li>
 *     <li><b>Discriminator</b> — a field annotated with {@code @Discriminator} requires an
 *     explicit discriminator value and delegates construction to the subtype declared by that
 *     enum constant;</li>
 *     <li><b>Sealed</b> — if the class is sealed, the owner of the non-null attributes determines
 *     which immediate subtype to instantiate; construction is delegated to that subtype's regular
 *     strategy;</li>
 *     <li><b>Bean</b> — a public no-argument constructor, with attributes applied via setters.</li>
 * </ol>
 * <p>
 * For non-sealed classes, candidate selection is strict: ambiguity throws an exception unless it
 * can be resolved by a single compatible candidate, a single exact match, or a single
 * {@code @Canonical} candidate.
 * <p>
 * For sealed classes, the single subtype that owns all non-null attributes is selected;
 * if none or more than one subtype qualifies, an exception is thrown. Construction is
 * then delegated to that subtype's regular strategy.
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
			if (type.isArray())
				return new ArrayStrategy();

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

			var candidates = Stream.concat(Arrays.stream(type.getConstructors())
									.filter(c -> !c.isAnnotationPresent(Deprecated.class)),
							Arrays.stream(type.getDeclaredMethods())
									.filter(m -> Modifier.isPublic(m.getModifiers()))
									.filter(m -> Modifier.isStatic(m.getModifiers()))
									.filter(m -> !m.isAnnotationPresent(Deprecated.class))
									.filter(m -> m.getName().equals("of")))
					.toList();

			var canonical = candidates.stream()
					.filter(c -> c.isAnnotationPresent(Canonical.class)).toList();
			if (canonical.size() > 1)
				throw new ConstructionException(type, canonical);
			if (canonical.size() == 1)
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

			candidates = candidates.stream()
					.filter(c -> matchesAttributes(attributes, c.getParameters()))
					.toList();
			if (candidates.size() == 1)
				if (candidates.get(0) instanceof Constructor<?> constructor)
					return new CanonicalConstructorStrategy(constructor);
				else if (candidates.get(0) instanceof Method method)
					return new CanonicalFactoryMethodStrategy(method);

			var exact = candidates.stream().filter(c -> c.getParameterCount() == attributes.size()).toList();
			if (exact.size() == 1)
				if (exact.get(0) instanceof Constructor<?> constructor)
					return new ConstructorStrategy(constructor);
				else if (exact.get(0) instanceof Method method)
					return new FactoryMethodStrategy(method);

			if (candidates.isEmpty())
			{
				var discriminator = Discriminator.Extractor.extract(type);
				if (discriminator != null)
					return new DiscriminatorConstructorStrategy(discriminator);

				if (type.isSealed())
				{
					var owners = new LinkedHashMap<Attribute, Class<?>>();
					for (var attribute : attributes)
					{
						var owner = attribute.getOwner();
						if (owner == null || !type.isAssignableFrom(owner))
							throw new ConstructionException(type, attributes);
						if (owner != type)
						{
							var subtypes = Arrays.stream(type.getPermittedSubclasses())
									.filter(e -> e.isAssignableFrom(owner))
									.toList();
							if (subtypes.size() != 1)
								throw new ConstructionException(type, attributes);
							owners.put(attribute, subtypes.get(0));
						} else
							owners.put(attribute, type);
					}
					return new SealedConstructorStrategy(type, owners);
				}

				var defaultConstructor = Arrays.stream(type.getConstructors())
						.filter(c -> c.getParameterCount() == 0)
						.findFirst()
						.orElse(null);
				if (defaultConstructor != null)
					return new BeanStrategy(defaultConstructor);

				throw new ConstructionException(type, attributes);
			}

			throw new ConstructionException(type, attributes, candidates);
		});
	}

	private static boolean matchesAttributes(Set<Attribute> attributes, Parameter[] parameters)
	{
		return attributes.stream().allMatch(a -> Arrays.stream(parameters).anyMatch(a::matches));
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
}