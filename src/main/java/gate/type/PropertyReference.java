package gate.type;

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

    Map<Class<?>, Info> INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * Returns metadata extracted from this property reference.
     *
     * @return metadata extracted from this property reference
     */
    default Info info()
    {
        return INFO_CACHE.computeIfAbsent(getClass(),
                unused -> createInfo(this));
    }

    /**
     * Extracts the {@link SerializedLambda} representation of this property reference.
     *
     * @return serialized lambda backing this reference
     */
    default SerializedLambda serializedLambda()
    {
        return info().serializedLambda();
    }

    /**
     * Returns the owner class that declares the referenced method.
     *
     * @return owner class of the referenced method
     */
    default Class<?> getOwnerClass()
    {
        return info().ownerClass();
    }

    /**
     * Returns the referenced zero-argument method when it can be resolved.
     *
     * @return referenced method, or empty when it cannot be resolved
     */
    default Optional<Method> getMethod()
    {
        return info().method();
    }

    private static Info createInfo(PropertyReference<?, ?> reference)
    {
        SerializedLambda lambda = extractSerializedLambda(reference);
        Class<?> ownerClass = loadOwnerClass(lambda);

        String methodName = lambda.getImplMethodName();
        Optional<Method> method = Optional.empty();
        for (Method candidate : ownerClass.getMethods())
            if (candidate.getName().equals(methodName) && candidate.getParameterCount() == 0)
            {
                method = Optional.of(candidate);
                break;
            }

        return new Info(lambda, ownerClass, method);
    }

    private static SerializedLambda extractSerializedLambda(PropertyReference<?, ?> reference)
    {
        try
        {
            Method method = reference.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            Object replacement = method.invoke(reference);

            if (replacement instanceof SerializedLambda lambda)
                return lambda;

            throw new IllegalStateException("Could not extract serialized lambda");
        } catch (ReflectiveOperationException ex)
        {
            throw new IllegalStateException("Could not extract serialized lambda", ex);
        }
    }

    private static Class<?> loadOwnerClass(SerializedLambda lambda)
    {
        try
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return Class.forName(lambda.getImplClass().replace('/', '.'),
                    false, classLoader);
        } catch (ClassNotFoundException ex)
        {
            throw new IllegalStateException("Could not load method owner class", ex);
        }
    }

    /**
     * Metadata extracted from a property reference.
     */
    record Info(SerializedLambda serializedLambda, Class<?> ownerClass, Optional<Method> method)
    {
    }
}
