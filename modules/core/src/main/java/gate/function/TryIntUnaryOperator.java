package gate.function;

import java.util.function.IntUnaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryIntUnaryOperator {

    int applyAsInt(int operand);

    public static IntUnaryOperator wrap(TryIntUnaryOperator operator) {
        return (operand) -> {
            try {
                return operator.applyAsInt(operand);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
