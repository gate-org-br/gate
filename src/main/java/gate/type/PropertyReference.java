package gate.type;

import gate.annotation.Entity;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
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

        if (methodName.startsWith("get") && methodName.length() > 3)
            return findMethod(lambda, methodName).map(Method::getReturnType)
                    .filter(e -> e.isAnnotationPresent(Entity.class))
                    .map(e -> e.getSimpleName() + "$" + e.getAnnotation(Entity.class).value())
                    .orElseGet(() -> Introspector.decapitalize(methodName.substring(3)));

        if (methodName.startsWith("is") && methodName.length() > 2)
            return Introspector.decapitalize(methodName.substring(2));

        return findMethod(lambda, methodName).map(Method::getReturnType)
                .filter(e -> e.isAnnotationPresent(Entity.class))
                .map(e -> e.getSimpleName() + "$" + e.getAnnotation(Entity.class).value())
                .orElse(methodName);
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

    private static Optional<Method> findMethod(SerializedLambda lambda, String methodName)
    {
        try
        {
            Class<?> owner = loadOwner(lambda);
            for (Method method : owner.getMethods())
                if (method.getName().equals(methodName) && method.getParameterCount() == 0)
                    return Optional.of(method);
            return Optional.empty();
        } catch (ClassNotFoundException ex)
        {
            throw new IllegalStateException("Could not load method owner class", ex);
        }
    }

    private static Class<?> loadOwner(SerializedLambda lambda)
            throws ClassNotFoundException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Class.forName(lambda.getImplClass().replace('/', '.'),
                false, classLoader);
    }
}
