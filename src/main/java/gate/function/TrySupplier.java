package gate.function;

import java.util.function.Supplier;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TrySupplier<T> {

    T get() throws Exception;

    public static <T> Supplier<T> wrap(TrySupplier<? extends T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
