package gate.function;


import java.util.function.Consumer;

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
                throw new UncheckedException(e);
            }
        };
    }
}
