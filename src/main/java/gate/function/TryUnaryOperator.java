package gate.function;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface TryUnaryOperator<T> extends TryFunction<T, T> {

    public static <T> UnaryOperator<T> wrap(TryUnaryOperator<T> operator) {
        return (t) -> {
            try {
                return operator.apply(t);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
