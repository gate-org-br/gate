package gate.function;

import java.util.function.DoubleBinaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryDoubleBinaryOperator {
    int applyAsDouble(double left, double right);

    public static DoubleBinaryOperator wrap(TryDoubleBinaryOperator operator) {
        return (t, u) -> {
            try {
                return operator.applyAsDouble(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
