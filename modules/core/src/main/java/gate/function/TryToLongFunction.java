package gate.function;

import java.util.function.ToLongFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
public interface TryToLongFunction<T> {

    long applyAsLong(T t) throws Exception;

    public static <T> ToLongFunction<T> wrap(TryToLongFunction<T> function) {
        return t -> {
            try {
                return function.applyAsLong(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
