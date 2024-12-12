package gate.function;

import java.util.function.ToLongFunction;

public interface TryToLongFunction<T> {

    long applyAsLong(T t) throws Exception;

    public static <T> ToLongFunction<T> wrap(TryToLongFunction<T> function) {
        return t -> {
            try {
                return function.applyAsLong(t);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
