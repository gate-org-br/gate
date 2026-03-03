package gate.function;

@FunctionalInterface
public interface ToCharFunction<T>
{
	char applyAsChar(T value);
}
