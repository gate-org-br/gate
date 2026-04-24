package gate.function;

import java.util.function.LongUnaryOperator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryLongUnaryOperator {

    long applyAsLong(long operand);

    public static LongUnaryOperator wrap(TryLongUnaryOperator operator) {
        return (operand) -> {
            try {
                return operator.applyAsLong(operand);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
