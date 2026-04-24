package gate.type;

import gate.util.Reflection;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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

	/**
	 * Metadata extracted from a property reference.
	 */
	record Metadata(SerializedLambda serializedLambda,
	                Class<?> ownerClass,
	                Field field,
	                Method method)
	{
		private static final Map<Class<?>, Metadata>
				CACHE = new ConcurrentHashMap<>();

		private static Metadata of(PropertyReference<?, ?> reference)
		{
			return CACHE.computeIfAbsent(reference.getClass(), r ->
			{
				try
				{
					Method writeReplace = reference.getClass().getDeclaredMethod("writeReplace");
					writeReplace.setAccessible(true);
					Object replacement = writeReplace.invoke(reference);

					if (!(replacement instanceof SerializedLambda lambda))
						throw new IllegalStateException("Could not extract serialized lambda");

					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					var className = lambda.getImplClass().replace('/', '.');
					Class<?> ownerClass = Class.forName(className, false, classLoader);

					Method accessor = Reflection.findMethod(ownerClass, lambda.getImplMethodName())
							.orElseThrow(() -> new IllegalStateException("Could not find accessor method %s on %s"
									.formatted(lambda.getImplMethodName(), ownerClass.getName())));

					Field field = Reflection.findField(accessor).orElse(null);

					return new Metadata(lambda, ownerClass, field, accessor);
				} catch (ReflectiveOperationException ex)
				{
					throw new IllegalStateException("Invalid property reference. Only direct method references to accessors are supported.", ex);
				}
			});
		}
	}
}