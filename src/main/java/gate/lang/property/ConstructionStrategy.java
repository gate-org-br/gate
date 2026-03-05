package gate.lang.property;

import gate.error.ConversionException;
import gate.function.UncheckedException;
import gate.util.Reflection;
import org.apache.commons.lang3.function.TriFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

interface ConstructionStrategy
{
	Object construct(Class<?> type,
					 Object value,
					 Map<Attribute, Object> propertyMap,
					 TriFunction<Attribute, Object, Object, Object> getValue)
			throws ReflectiveOperationException;

	Map<Set<Attribute>, ConstructionStrategy> CACHE = new ConcurrentHashMap<>();

	static Object newInstance(Class<?> type,
							  Object value,
							  Map<Attribute, Object> propertyMap,
							  TriFunction<Attribute, Object, Object, Object> getValue) throws ReflectiveOperationException
	{
		return CACHE.computeIfAbsent(propertyMap.keySet(), attributes ->
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

						if (ObjectFactory.canCreate(type))
							return new BeanStrategy();

						var candidates = Arrays.stream(type.getDeclaredConstructors())
								.filter(c ->
								{
									var params = c.getParameters();
									return params.length == propertyMap.size()
											&& Arrays.stream(params).allMatch(p -> attributes.stream().anyMatch(a -> a.matches(p)));
								})
								.toList();

						if (candidates.size() != 1)
							throw new ConversionException("Could not find construction strategy for " + type.getName());

						return new CanonicalConstructorStrategy(candidates.get(0));
					} catch (ReflectiveOperationException ex)
					{
						throw new UncheckedException(ex);
					}
				})
				.construct(type, value, propertyMap, getValue);
	}


	record BuilderStrategy(Method builderFactory, Method build) implements ConstructionStrategy
	{
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
							.orElseThrow())
					.toArray();
			return constructor.newInstance(args);
		}
	}

	record RecordStrategy(Constructor<?> constructor) implements ConstructionStrategy
	{
		@Override
		public Object construct(Class<?> type,
								Object value,
								Map<Attribute, Object> propertyMap,
								TriFunction<Attribute, Object, Object, Object> getValue)
				throws ReflectiveOperationException
		{
			var args = Arrays.stream(constructor.getParameters())
					.map(c -> propertyMap.entrySet().stream()
							.filter(e -> e.getKey().toString().equals(c.getName()))
							.findFirst()
							.map(e -> getValue.apply(e.getKey(), null, e.getValue()))
							.orElse(null))
					.toArray();
			return constructor.newInstance(args);
		}
	}
}