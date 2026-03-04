package gate.function;

import java.util.function.DoubleUnaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryDoubleUnaryOperator {

    double applyAsDouble(double operand);

    public static DoubleUnaryOperator wrap(TryDoubleUnaryOperator operator) {
        return (operand) -> {
            try {
                return operator.applyAsDouble(operand);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
