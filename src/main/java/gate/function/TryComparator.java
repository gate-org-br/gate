package gate.function;

import java.util.Comparator;

/**
 * Functional interface variant that allows checked exceptions and can be wrapped into the corresponding JDK functional type.
 */
@FunctionalInterface
public interface TryComparator<T> {
    int compare(T o1, T o2) throws Exception;

    public static <T> Comparator<T> wrap(TryComparator<T> comparator) {
        return (t1, t2) -> {
            try {
                return comparator.compare(t1, t2);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
