package gate.stream;

@FunctionalInterface
public interface CheckedPredicate<T>
{

	public boolean test(T object) throws Exception;

}
