package gate.stream;

public interface CheckedToLongFunction<T>
{

	long applyAsLong(T value) throws Exception;
}
