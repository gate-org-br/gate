package gate.function;

import java.util.function.LongSupplier;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryLongSupplier {

    long get() throws Exception;

    public static LongSupplier wrap(TryLongSupplier supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
