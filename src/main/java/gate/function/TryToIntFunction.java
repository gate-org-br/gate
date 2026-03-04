package gate.function;

import java.util.function.ToIntFunction;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
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
