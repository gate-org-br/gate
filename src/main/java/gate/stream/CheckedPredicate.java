package gate.stream;

@FunctionalInterface
public interface CheckedPredicate<T>
{

	boolean test(T object) throws Exception;

}
