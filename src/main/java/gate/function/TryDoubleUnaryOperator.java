package gate.function;

import java.util.function.DoubleUnaryOperator;

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
