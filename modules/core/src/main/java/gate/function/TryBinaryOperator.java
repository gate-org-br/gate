package gate.function;

import java.util.function.BinaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryBinaryOperator<T> extends TryBiFunction<T, T, T> {

    public static <T> BinaryOperator<T> wrap(TryBinaryOperator<T> operator) {
        return (t, u) -> {
            try {
                return operator.apply(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
