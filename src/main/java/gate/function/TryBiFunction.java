package gate.function;

import java.util.function.BiFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryBiFunction<T, U, R> {

    R apply(T t, U u) throws Exception;

    public static <T, U, R> BiFunction<T, U, R> wrap(TryBiFunction<T, U, R> function) {
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
