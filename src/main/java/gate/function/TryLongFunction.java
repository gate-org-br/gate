package gate.function;

import java.util.function.LongFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryLongFunction<T> {

    T apply(long t) throws Exception;

    static <T> LongFunction<T> wrap(TryLongFunction<T> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
