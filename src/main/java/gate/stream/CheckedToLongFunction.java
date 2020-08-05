package gate.stream;

public interface CheckedToLongFunction<T>
{

	public long applyAsLong(T value) throws Exception;
}
