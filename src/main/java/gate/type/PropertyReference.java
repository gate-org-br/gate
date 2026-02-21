package gate.type;

import gate.annotation.Entity;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@FunctionalInterface
public interface PropertyReference<T, R> extends Function<T, R>, Serializable
{
	Map<String, String> CACHE = new ConcurrentHashMap<>();

	static String property(PropertyReference<?, ?> reference)
	{
		SerializedLambda lambda = reference.serializedLambda();
		String key = lambda.getImplClass() + "#" + lambda.getImplMethodName();
		return CACHE.computeIfAbsent(key, unused -> resolvePropertyName(reference, lambda));
	}

	private static String resolvePropertyName(PropertyReference<?, ?> reference, SerializedLambda lambda)
	{
		String methodName = lambda.getImplMethodName();

        if (methodName.startsWith("lambda$"))
            throw new IllegalStateException("PropertyReference must be a method reference");

		String entityReference = reference.entityReferenceProperty(lambda, methodName);
		if (entityReference != null)
			return entityReference;

		if (reference.isRecordAccessor(lambda, methodName))
			return methodName;

        if (methodName.startsWith("get") && methodName.length() > 3)
            return Introspector.decapitalize(methodName.substring(3));

        if (methodName.startsWith("is") && methodName.length() > 2)
            return Introspector.decapitalize(methodName.substring(2));

		return methodName;
	}

	private String entityReferenceProperty(SerializedLambda lambda, String methodName)
	{
		if (!methodName.startsWith("get") || methodName.length() <= 3)
			return null;

		Method method = findMethod(lambda, methodName);
		if (method == null)
			return null;

		Entity entity = method.getReturnType().getAnnotation(Entity.class);
		if (entity == null)
			return null;

		String key = entity.value() == null || entity.value().isBlank()
				? "id"
				: entity.value();

		return methodName.substring(3) + "$" + key;
	}

    private SerializedLambda serializedLambda()
    {
        try
        {
            Method method = getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            Object replacement = method.invoke(this);

            if (replacement instanceof SerializedLambda)
                return (SerializedLambda) replacement;

            throw new IllegalStateException("Could not extract serialized lambda");
        } catch (ReflectiveOperationException ex)
        {
            throw new IllegalStateException("Could not extract serialized lambda", ex);
        }
    }

    private boolean isRecordAccessor(SerializedLambda lambda, String methodName)
    {
        try
        {
            Class<?> owner = loadOwner(lambda);

            if (!owner.isRecord())
                return false;

            for (RecordComponent component : owner.getRecordComponents())
                if (component.getName().equals(methodName))
                    return true;

            return false;
        } catch (ClassNotFoundException ex)
        {
            throw new IllegalStateException("Could not load method owner class", ex);
        }
    }

	private Method findMethod(SerializedLambda lambda, String methodName)
	{
		try
		{
			Class<?> owner = loadOwner(lambda);
			for (Method method : owner.getMethods())
				if (method.getName().equals(methodName) && method.getParameterCount() == 0)
					return method;
			return null;
		} catch (ClassNotFoundException ex)
		{
			throw new IllegalStateException("Could not load method owner class", ex);
		}
	}

	private Class<?> loadOwner(SerializedLambda lambda) throws ClassNotFoundException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return Class.forName(lambda.getImplClass().replace('/', '.'), false, classLoader);
	}
}
