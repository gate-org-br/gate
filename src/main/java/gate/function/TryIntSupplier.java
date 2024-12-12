package gate.function;

import java.util.function.IntSupplier;

@FunctionalInterface
public interface TryIntSupplier {

    int get() throws Exception;

    public static IntSupplier wrap(TryIntSupplier supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
