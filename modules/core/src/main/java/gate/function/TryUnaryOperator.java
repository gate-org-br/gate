package gate.function;

import java.util.function.UnaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryUnaryOperator<T> extends TryFunction<T, T> {

    public static <T> UnaryOperator<T> wrap(TryUnaryOperator<T> operator) {
        return (t) -> {
            try {
                return operator.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
