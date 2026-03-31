package gate.function;

import java.util.function.Predicate;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
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
                throw new RuntimeException(e);
            }
        };
    }
}
