package gate.function;

import java.util.function.DoublePredicate;
import java.util.function.LongPredicate;

@FunctionalInterface
public interface TryDoublePredicate {

    boolean test(double t) throws Exception;

    public static DoublePredicate wrap(TryDoublePredicate predicate) {
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
