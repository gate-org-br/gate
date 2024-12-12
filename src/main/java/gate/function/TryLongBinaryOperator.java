package gate.function;

import java.util.function.LongBinaryOperator;

@FunctionalInterface
public interface TryLongBinaryOperator {
    long applyAsLong(long left, long right);

    public static LongBinaryOperator wrap(TryLongBinaryOperator operator) {
        return (t, u) -> {
            try {
                return operator.applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
