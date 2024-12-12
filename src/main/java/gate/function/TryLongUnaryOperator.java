package gate.function;

import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface TryLongUnaryOperator {

    long applyAsLong(long operand);

    public static LongUnaryOperator wrap(TryLongUnaryOperator operator) {
        return (operand) -> {
            try {
                return operator.applyAsLong(operand);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
