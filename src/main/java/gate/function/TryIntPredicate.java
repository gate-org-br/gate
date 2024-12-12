package gate.function;

import java.util.function.IntPredicate;

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
                throw new UncheckedException(e);
            }
        };
    }
}
