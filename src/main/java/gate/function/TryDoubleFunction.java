package gate.function;

import java.util.function.DoubleFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryDoubleFunction<T> {

    T apply(double t) throws Exception;

    static <T> DoubleFunction<T> wrap(TryDoubleFunction<T> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
