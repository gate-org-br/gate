package gate.function;

import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface TryIntUnaryOperator {

    int applyAsInt(int operand);

    public static IntUnaryOperator wrap(TryIntUnaryOperator operator) {
        return (operand) -> {
            try {
                return operator.applyAsInt(operand);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
