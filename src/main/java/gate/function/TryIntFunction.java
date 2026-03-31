package gate.function;

import java.util.function.IntFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryIntFunction<T> {

    T apply(int t) throws Exception;

    static <T> IntFunction<T> wrap(TryIntFunction<T> function) {
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
