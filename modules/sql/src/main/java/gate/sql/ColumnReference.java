package gate.sql;

import gate.sql.annotation.Column;
import gate.annotation.Entity;
import gate.type.PropertyReference;
import gate.util.Reflection;

import java.beans.Introspector;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Resolves SQL column names from {@link PropertyReference} method references.
 */
public record ColumnReference<T, R>(PropertyReference<T, R> property, String name,
                                    Function<R, ?> extractor)
{

	private static final Map<Class<?>, ColumnReference<?, ?>> CACHE = new ConcurrentHashMap<>();

	/**
	 * Resolves the SQL column name represented by the given property reference.
	 *
	 * @param property property reference to resolve
	 * @return resolved SQL column name
	 */
	@SuppressWarnings("unchecked")
	public static <T, R> ColumnReference<T, R> of(PropertyReference<T, R> property)
	{
		return (ColumnReference<T, R>) CACHE.computeIfAbsent(property.getClass(), unused ->
				new ColumnReference<>(property, getColumnName(property), getExtractor(property)));
	}

	private static String getColumnName(PropertyReference<?, ?> reference)
	{
		var info = reference.metadata();

		if (info.method().isAnnotationPresent(Column.class))
			return info.method().getAnnotation(Column.class).value();
		if (info.field() != null && info.field().isAnnotationPresent(Column.class))
			return info.field().getAnnotation(Column.class).value();

		var type = info.method().getReturnType();
		var methodName = info.method().getName();
		if (type.isAnnotationPresent(Entity.class))
			if (methodName.startsWith("get") && methodName.length() > 3)
				return methodName.substring(3) + "$"
				       + type.getAnnotation(Entity.class).value();
			else
				return Character.toUpperCase(methodName.charAt(0))
				       + methodName.substring(1) + "$"
				       + type.getAnnotation(Entity.class).value();

		if (methodName.startsWith("get") && methodName.length() > 3)
			return Introspector.decapitalize(methodName.substring(3));

		if (methodName.startsWith("is") && methodName.length() > 2)
			return Introspector.decapitalize(methodName.substring(2));

		return methodName;
	}

	private static <R> Function<R, ?> getExtractor(PropertyReference<?, R> reference)
	{
		var info = reference.metadata();
		var type = info.method().getReturnType();
		if (type.isAnnotationPresent(Entity.class))
		{
			try
			{
				Entity annotation = type.getAnnotation(Entity.class);
				Method method = Reflection.findGetterByName(type, annotation.value()).orElseThrow();
				MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
				MethodHandle handle = lookup.unreflect(method);

				return e ->
				{
					try
					{
						return e != null ? handle.invoke(e) : null;
					} catch (Throwable ex)
					{
						throw new RuntimeException(ex);
					}
				};
			} catch (Throwable e)
			{
				throw new IllegalArgumentException("Invalid entity reference type %s: could not resolve identifier accessor"
						.formatted(type.getName()), e);
			}
		}

		return Function.identity();
	}
}
