package gate.function;


import java.util.function.Consumer;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryConsumer<T> {

    void accept(T t) throws Exception;

    public static <T> Consumer<T> wrap(TryConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
