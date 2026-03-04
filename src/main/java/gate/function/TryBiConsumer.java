package gate.function;

import java.util.function.BiConsumer;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
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
