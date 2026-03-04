package gate.function;


import java.util.function.DoubleConsumer;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
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
