package gate.function;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface TryBiConsumer<T, U> {

    void accept(T t, U u) throws Exception;

    public static <T, U> BiConsumer<T, U> wrap(TryBiConsumer<T, U> consumer) {
        return (t, u) -> {
            try {
                consumer.accept(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
