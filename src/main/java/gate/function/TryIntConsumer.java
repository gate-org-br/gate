package gate.function;


import java.util.function.IntConsumer;

@FunctionalInterface
public interface TryIntConsumer {

    void accept(int t) throws Exception;

    public static IntConsumer wrap(TryIntConsumer consumer) {
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
