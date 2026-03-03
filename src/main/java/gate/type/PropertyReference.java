package gate.type;

import gate.annotation.Entity;

import java.io.Serializable;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface PropertyReference<T, R> extends Function<T, R>, Serializable
{
	/**
	 * Returns metadata extracted from this property reference.
	 *
	 * @return metadata extracted from this property reference
	 */
	default Metadata metadata()
	{
		return Metadata.of(this);
	}

	default Object identity(Object value)
	{
		if (metadata().identity != null)
			return metadata().identity.apply(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	static PropertyReference<Object, Object> of(Method method)
	{
		if (method.getParameterCount() != 0)
			throw new IllegalArgumentException(method + " is not a zero-arg getter");

		if ((method.getModifiers() & Modifier.STATIC) != 0)
			throw new IllegalArgumentException(method + " is static, not a getter");

		Class<?> owner = method.getDeclaringClass();

		try
		{
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, MethodHandles.lookup());
			MethodHandle impl = lookup.unreflect(method);
			MethodType sam = MethodType.methodType(Object.class, Object.class);
			MethodType instantiated = MethodType.methodType(method.getReturnType(), owner);

			return (PropertyReference<Object, Object>) LambdaMetafactory
					.metafactory(lookup,
							"apply",
							MethodType.methodType(PropertyReference.class),
							sam,
							impl,
							instantiated
					).getTarget().invokeExact();
		} catch (Throwable e)
		{
			throw new IllegalArgumentException(method + " is not a valid getter", e);
		}
	}

	/**
	 * Metadata extracted from a property reference.
	 */
	record Metadata(SerializedLambda serializedLambda,
					Class<?> ownerClass,
					Class<?> type,
					PropertyReference<Object, Object> identity)
	{
		private static final Map<Class<?>, Metadata>
				CACHE = new ConcurrentHashMap<>();

		private static Metadata of(PropertyReference<?, ?> reference)
		{
			return CACHE.computeIfAbsent(reference.getClass(), r ->
			{
				try
				{
					Method method = reference.getClass().getDeclaredMethod("writeReplace");
					method.setAccessible(true);
					Object replacement = method.invoke(reference);

					if (!(replacement instanceof SerializedLambda lambda))
						throw new IllegalStateException("Could not extract serialized lambda");

					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					var className = lambda.getImplClass().replace('/', '.');
					Class<?> ownerClass = Class.forName(className, false, classLoader);

					Class<?> type = Stream.of(ownerClass.getMethods())
							.filter(e -> e.getName().equals(lambda.getImplMethodName()))
							.filter(e -> e.getParameterCount() == 0)
							.findFirst()
							.map(Method::getReturnType)
							.orElseThrow();
					return new Metadata(lambda, ownerClass, type, Entity.Extractor.get(type));
				} catch (ReflectiveOperationException ex)
				{
					throw new IllegalStateException("Could not extract serialized lambda", ex);
				}
			});
		}
	}
}
