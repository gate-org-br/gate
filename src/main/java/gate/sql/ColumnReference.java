package gate.sql;

import gate.annotation.Entity;
import gate.type.PropertyReference;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves SQL column names from {@link PropertyReference} method references.
 */
public final class ColumnReference
{

	private static final Map<Class<?>, String> CACHE = new ConcurrentHashMap<>();

	private ColumnReference()
	{
	}

	/**
	 * Resolves the SQL column name represented by the given property reference.
	 *
	 * @param reference property reference to resolve
	 * @return resolved SQL column name
	 */
	public static String of(PropertyReference<?, ?> reference)
	{
		return CACHE.computeIfAbsent(reference.getClass(), unused ->
		{
			var info = reference.metadata();

			SerializedLambda lambda = info.serializedLambda();
			String methodName = lambda.getImplMethodName();

			if (methodName.startsWith("lambda$"))
				throw new IllegalArgumentException("PropertyReference must be a method reference");

			if (info.type().isAnnotationPresent(Entity.class))
				if (methodName.startsWith("get") && methodName.length() > 3)
					return methodName.substring(3) + "$"
							+ info.type().getAnnotation(Entity.class).value();
				else
					return Character.toUpperCase(methodName.charAt(0))
							+ methodName.substring(1) + "$"
							+ info.type().getAnnotation(Entity.class).value();

			if (methodName.startsWith("get") && methodName.length() > 3)
				return Introspector.decapitalize(methodName.substring(3));

			if (methodName.startsWith("is") && methodName.length() > 2)
				return Introspector.decapitalize(methodName.substring(2));

			return methodName;
		});
	}
}
