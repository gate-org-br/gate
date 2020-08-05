package gate.stream;

public interface CheckedToDoubleFunction<T>
{

	public double applyAsDouble​(T value) throws Exception;
}
