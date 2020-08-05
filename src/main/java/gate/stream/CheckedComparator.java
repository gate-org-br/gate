package gate.stream;

@FunctionalInterface
public interface CheckedComparator<T>
{

	public int compare(T o1, T o2) throws Exception;

}
