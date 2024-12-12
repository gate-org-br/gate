package gate.function;

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
