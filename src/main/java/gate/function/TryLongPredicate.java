package gate.function;

import java.util.function.LongPredicate;

@FunctionalInterface
public interface TryLongPredicate {

    boolean test(long t) throws Exception;

    public static LongPredicate wrap(TryLongPredicate predicate) {
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
