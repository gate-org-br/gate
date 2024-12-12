package gate.function;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

@FunctionalInterface
public interface TryDoubleSupplier {

    double get() throws Exception;

    public static DoubleSupplier wrap(TryDoubleSupplier supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
