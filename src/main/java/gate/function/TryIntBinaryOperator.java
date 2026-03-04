package gate.function;

import java.util.function.IntBinaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryIntBinaryOperator {
    int applyAsInt(int left, int right);

    public static IntBinaryOperator wrap(TryIntBinaryOperator operator) {
        return (t, u) -> {
            try {
                return operator.applyAsInt(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
