package gate.stream;

public interface CheckedToIntFunction<T>
{

	int applyAsInt(T value) throws Exception;
}
