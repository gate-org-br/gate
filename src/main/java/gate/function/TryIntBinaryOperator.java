package gate.function;

import java.util.function.IntBinaryOperator;

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
