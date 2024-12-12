package gate.function;

import java.util.function.Function;

@FunctionalInterface
public interface TryFunction<T, R> {

    R apply(T t) throws Exception;

    public static <T, R> Function<T, R> wrap(TryFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
