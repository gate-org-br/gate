package gate.function;

import java.util.function.IntPredicate;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryIntPredicate {

    boolean test(int t) throws Exception;

    public static IntPredicate wrap(TryIntPredicate predicate) {
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
