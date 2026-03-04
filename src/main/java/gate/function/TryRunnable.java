package gate.function;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryRunnable {

    void run() throws Exception;

    public static Runnable wrap(TryRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
