package gate.sql;

import gate.annotation.Entity;
import gate.type.PropertyReference;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
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
        return CACHE.computeIfAbsent(reference.getClass(),
                unused -> resolvePropertyName(reference.info()));
    }

    private static String resolvePropertyName(PropertyReference.Info info)
    {
        SerializedLambda lambda = info.serializedLambda();
        String methodName = lambda.getImplMethodName();

        if (methodName.startsWith("lambda$"))
            throw new IllegalArgumentException("PropertyReference must be a method reference");

        if (methodName.startsWith("get") && methodName.length() > 3)
            return info.method().map(Method::getReturnType)
                    .filter(e -> e.isAnnotationPresent(Entity.class))
                    .map(e -> e.getSimpleName() + "$" + e.getAnnotation(Entity.class).value())
                    .orElseGet(() -> Introspector.decapitalize(methodName.substring(3)));

        if (methodName.startsWith("is") && methodName.length() > 2)
            return Introspector.decapitalize(methodName.substring(2));

        return info.method().map(Method::getReturnType)
                .filter(e -> e.isAnnotationPresent(Entity.class))
                .map(e -> e.getSimpleName() + "$" + e.getAnnotation(Entity.class).value())
                .orElse(methodName);
    }
}
