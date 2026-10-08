package gate.function;

import java.util.function.IntSupplier;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryIntSupplier {

    int get() throws Exception;

    public static IntSupplier wrap(TryIntSupplier supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
