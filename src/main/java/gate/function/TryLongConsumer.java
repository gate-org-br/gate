package gate.function;


import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryLongConsumer {

    void accept(long t) throws Exception;

    public static LongConsumer wrap(TryLongConsumer consumer) {
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
