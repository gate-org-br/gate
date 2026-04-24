package gate.function;

@FunctionalInterface
public interface ToByteFunction<T>
{
	byte applyAsByte(T value);
}
