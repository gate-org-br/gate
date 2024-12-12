package gate.function;

import java.util.function.ToIntFunction;

public interface TryToIntFunction<T> {

    int applyAsInt(T t) throws Exception;

    public static <T> ToIntFunction<T> wrap(TryToIntFunction<T> function) {
        return t -> {
            try {
                return function.applyAsInt(t);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
