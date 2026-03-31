package gate.lang.property;

import gate.annotation.Canonical;
import gate.error.ConversionException;
import gate.util.Reflection;
import org.apache.commons.lang3.function.TriFunction;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Strategy used to construct objects from a set of {@link Attribute} values.
 * <p>
 * The resolution is structural: a constructor or static {@code of(...)} factory is considered
 * compatible when every provided attribute matches one of its parameters. Attributes must not be
 * ignored by the selected strategy.
 * <p>
 * Missing attributes are handled according to the construction model of the target type:
 * <ul>
 *     <li>records, constructors and static factories receive {@code null} for unmatched parameters;</li>
 *     <li>builders receive only the attributes that were explicitly provided;</li>
 *     <li>beans are created or reused and only the provided attributes are applied.</li>
 * </ul>
 * This allows immutable types to enforce their own invariants in their constructor or factory,
 * while still supporting partial builders and anemic mutable beans.
 */
public interface ConstructionStrategy
{
	Object construct(Class<?> type,
	                 Map<Attribute, Object> attributes)
			throws ReflectiveOperationException;

	Object construct(Class<?> type,
	                 Object value,
	                 Map<Attribute, Object> propertyMap,
	                 TriFunction<Attribute, Object, Object, Object> getValue)
			throws ReflectiveOperationException;

	Map<Set<Attribute>, ConstructionStrategy> CACHE = new ConcurrentHashMap<>();

	static ConstructionStrategy get(Class<?> type, Set<Attribute> attributes)
	{
		return CACHE.computeIfAbsent(attributes, e ->
		{
			try
			{
				if (type.isRecord())
				{
					var types = Arrays.stream(type.getRecordComponents())
							.map(RecordComponent::getType)
							.toArray(Class<?>[]::new);
					var constructor = type.getDeclaredConstructor(types);
					constructor.setAccessible(true);
					return new RecordStrategy(constructor);
				}

				var builderFactory = Reflection.findMethod(type, "builder").orElse(null);
				if (builderFactory != null)
				{
					var build = Reflection.findMethod(builderFactory.getReturnType(), "build").orElse(null);
					if (build != null)
						return new BuilderStrategy(builderFactory, build);
				}

				var constructors = Arrays.stream(type.getConstructors())
						.filter(c -> !c.isAnnotationPresent(Deprecated.class))
						.filter(c -> matchesAttributes(attributes, c.getParameters()))
						.toList();
				var factories = Arrays.stream(type.getDeclaredMethods())
						.filter(m -> Modifier.isPublic(m.getModifiers()))
						.filter(m -> Modifier.isStatic(m.getModifiers()))
						.filter(m -> !m.isAnnotationPresent(Deprecated.class))
						.filter(m -> m.getName().equals("of"))
						.filter(m -> matchesAttributes(attributes, m.getParameters()))
						.toList();

				var canonicalConstructor = constructors.stream()
						.filter(c -> c.isAnnotationPresent(Canonical.class)).toList();
				var canonicalFactory = factories.stream()
						.filter(m -> m.isAnnotationPresent(Canonical.class)).toList();

				if (canonicalConstructor.size() + canonicalFactory.size() > 1)
					throw new ConversionException("Ambiguous @Canonical for " + type.getName());
				if (!canonicalConstructor.isEmpty())
					return new CanonicalConstructorStrategy(canonicalConstructor.get(0));
				if (!canonicalFactory.isEmpty())
					return new FactoryMethodStrategy(canonicalFactory.get(0));

				if (factories.size() + constructors.size() > 1)
					throw new ConversionException("Ambiguous construction strategy for " + type.getName());
				if (!constructors.isEmpty())
					return new CanonicalConstructorStrategy(constructors.get(0));
				if (!factories.isEmpty())
					return new FactoryMethodStrategy(factories.get(0));

				if (ObjectFactory.canCreate(type))
					return new BeanStrategy();

				throw new ConversionException("Could not find construction strategy for " + type.getName());
			} catch (ReflectiveOperationException ex)
			{
				throw new RuntimeException(ex);
			}
		});
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
		return get(type, propertyMap.keySet())
				.construct(type, value, propertyMap, getValue);
	}

	static Object newInstance(Class<?> type,
	                          Map<Attribute, Object> attributes) throws ReflectiveOperationException
	{
		return get(type, attributes.keySet())
				.construct(type, attributes);
	}


	record BuilderStrategy(Method builderFactory, Method build) implements ConstructionStrategy
	{
		@Override
		public Object construct(Class<?> type, Map<Attribute, Object> attributes) throws ReflectiveOperationException
		{
			var builder = builderFactory.invoke(null);
			for (var entry : attributes.entrySet())
			{
				var attribute = entry.getKey();
				var method = Reflection.findMethod(builder.getClass(),
						attribute.toString(), attribute.getRawType()).orElseThrow();
				method.invoke(builder, entry.getValue());
			}
			return build.invoke(builder);
		}

		@Override
		public Object construct(Class<?> type,
		                        Object value,
		                        Map<Attribute, Object> propertyMap,
		                        TriFunction<Attribute, Object, Object, Object> getValue)
				throws ReflectiveOperationException
		{
			var builder = builderFactory.invoke(null);
			for (var entry : propertyMap.entrySet())
			{
				var attribute = entry.getKey();
				var newValue = getValue.apply(attribute, null, entry.getValue());
				Reflection.findMethod(builder.getClass(), attribute.toString(), attribute.getRawType())
						.orElseThrow()
						.invoke(builder, newValue);
			}
			return build.invoke(builder);
		}
	}

	record BeanStrategy() implements ConstructionStrategy
	{
		@Override
		public Object construct(Class<?> type, Map<Attribute, Object> attributes) throws ReflectiveOperationException
		{
			var value = ObjectFactory.create(type);
			for (var attribute : attributes.entrySet())
				attribute.getKey().setValue(value, attribute.getValue());
			return value;
		}

		@Override
		public Object construct(Class<?> type, Object value, Map<Attribute, Object> propertyMap,
		                        TriFunction<Attribute, Object, Object, Object> getValue)
				throws ReflectiveOperationException
		{
			if (value == null)
				value = ObjectFactory.create(type);
			for (var entry : propertyMap.entrySet())
			{
				var attribute = entry.getKey();
				var currentValue = attribute.getValue(value);
				var newValue = getValue.apply(attribute, currentValue, entry.getValue());
				if (newValue != currentValue)
					attribute.setValue(value, newValue);
			}
			return value;
		}
	}

	record CanonicalConstructorStrategy(Constructor<?> constructor) implements ConstructionStrategy
	{
		@Override
		public Object construct(Class<?> type, Map<Attribute, Object> attributes) throws ReflectiveOperationException
		{
			var values = Arrays.stream(constructor.getParameters())
					.map(p -> attributes.entrySet().stream()
							.filter(a -> a.getKey().matches(p))
							.findFirst()
							.map(Map.Entry::getValue)
							.orElse(null))
					.toArray();
			return constructor.newInstance(values);
		}

		@Override
		public Object construct(Class<?> type,
		                        Object value,
		                        Map<Attribute, Object> propertyMap,
		                        TriFunction<Attribute, Object, Object, Object> getValue)
				throws ReflectiveOperationException
		{
			Object[] args = Arrays.stream(constructor.getParameters())
					.map(p -> propertyMap.keySet().stream()
							.filter(a -> a.matches(p))
							.findFirst()
							.map(e -> getValue.apply(e, null, null))
							.orElse(null))
					.toArray();
			return constructor.newInstance(args);
		}
	}

	record RecordStrategy(Constructor<?> constructor) implements ConstructionStrategy
	{
		@Override
		public Object construct(Class<?> type, Map<Attribute, Object> attributes) throws ReflectiveOperationException
		{
			var entryset = attributes.entrySet();
			var values = Arrays.stream(constructor.getParameters())
					.map(c -> entryset.stream()
							.filter(e -> e.getKey().matches(c))
							.findFirst()
							.map(Map.Entry::getValue)
							.orElse(null))
					.toArray();
			return constructor.newInstance(values);
		}

		@Override
		public Object construct(Class<?> type,
		                        Object value,
		                        Map<Attribute, Object> propertyMap,
		                        TriFunction<Attribute, Object, Object, Object> getValue)
				throws ReflectiveOperationException
		{
			var args = Arrays.stream(constructor.getParameters())
					.map(c -> propertyMap.entrySet().stream()
							.filter(e -> e.getKey().matches(c))
							.findFirst()
							.map(e -> getValue.apply(e.getKey(), null, e.getValue()))
							.orElse(null))
					.toArray();
			return constructor.newInstance(args);
		}
	}

	record FactoryMethodStrategy(Method method) implements ConstructionStrategy
	{
		@Override
		public Object construct(Class<?> type, Map<Attribute, Object> attributes)
				throws ReflectiveOperationException
		{
			var values = Arrays.stream(method.getParameters())
					.map(p -> attributes.entrySet().stream()
							.filter(e -> e.getKey().matches(p))
							.findFirst()
							.map(Map.Entry::getValue)
							.orElse(null))
					.toArray();
			return method.invoke(null, values);
		}

		@Override
		public Object construct(Class<?> type,
		                        Object value,
		                        Map<Attribute, Object> propertyMap,
		                        TriFunction<Attribute, Object, Object, Object> getValue)
				throws ReflectiveOperationException
		{
			var args = Arrays.stream(method.getParameters())
					.map(p -> propertyMap.entrySet().stream()
							.filter(e -> e.getKey().matches(p))
							.findFirst()
							.map(e -> getValue.apply(e.getKey(), null, e.getValue()))
							.orElse(null))
					.toArray();
			return method.invoke(null, args);
		}
	}
}
