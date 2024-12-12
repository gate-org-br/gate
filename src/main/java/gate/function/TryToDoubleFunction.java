package gate.function;

import java.util.function.ToDoubleFunction;

public interface TryToDoubleFunction<T> {

    double applyAsDouble(T t) throws Exception;

    public static <T> ToDoubleFunction<T> wrap(TryToDoubleFunction<T> function) {
        return t -> {
            try {
                return function.applyAsDouble(t);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
