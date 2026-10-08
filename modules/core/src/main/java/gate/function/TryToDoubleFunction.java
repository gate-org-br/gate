package gate.function;

import java.util.function.ToDoubleFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
public interface TryToDoubleFunction<T> {

    double applyAsDouble(T t) throws Exception;

    public static <T> ToDoubleFunction<T> wrap(TryToDoubleFunction<T> function) {
        return t -> {
            try {
                return function.applyAsDouble(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
