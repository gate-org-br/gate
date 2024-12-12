package gate.function;

import java.util.function.Predicate;

@FunctionalInterface
public interface TryPredicate<T> {

    boolean test(T t) throws Exception;

    public static <T> Predicate<T> wrap(TryPredicate<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }
}
