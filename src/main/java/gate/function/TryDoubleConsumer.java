package gate.function;


import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface TryDoubleConsumer {

    void accept(double t) throws Exception;

    public static DoubleConsumer wrap(TryDoubleConsumer consumer) {
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
