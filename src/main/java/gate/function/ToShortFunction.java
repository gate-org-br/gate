package gate.function;

@FunctionalInterface
public interface ToShortFunction<T>
{
	short applyAsShort(T value);
}
