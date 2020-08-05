package gate.stream;

public interface CheckedToIntFunction<T>
{

	public int applyAsInt(T value) throws Exception;
}
