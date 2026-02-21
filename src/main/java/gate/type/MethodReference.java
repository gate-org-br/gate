package gate.type;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@FunctionalInterface
public interface MethodReference<T, R> extends Function<T, R>, Serializable
{
	Map<String, String> CACHE = new ConcurrentHashMap<>();

	static String property(MethodReference<?, ?> reference)
	{
		SerializedLambda lambda = reference.serializedLambda();
		String key = lambda.getImplClass() + "#" + lambda.getImplMethodName();
		return CACHE.computeIfAbsent(key, unused -> resolvePropertyName(reference, lambda));
	}

	private static String resolvePropertyName(MethodReference<?, ?> reference, SerializedLambda lambda)
	{
		String methodName = lambda.getImplMethodName();

        if (methodName.startsWith("lambda$"))
            throw new IllegalStateException("MethodReference must be a method reference");

		if (reference.isRecordAccessor(lambda, methodName))
			return methodName;

        if (methodName.startsWith("get") && methodName.length() > 3)
            return Introspector.decapitalize(methodName.substring(3));

        if (methodName.startsWith("is") && methodName.length() > 2)
            return Introspector.decapitalize(methodName.substring(2));

		return methodName;
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
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> owner = Class.forName(lambda.getImplClass()
                    .replace('/',
                            '.'), false, classLoader);

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
}
