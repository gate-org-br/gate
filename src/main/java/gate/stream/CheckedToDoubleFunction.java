package gate.stream;

public interface CheckedToDoubleFunction<T>
{

	double applyAsDouble(T value) throws Exception;
}
