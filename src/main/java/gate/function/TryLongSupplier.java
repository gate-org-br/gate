package gate.function;

import java.util.function.LongSupplier;

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
