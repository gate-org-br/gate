package gate.function;

@FunctionalInterface
public interface ObjByteConsumer<T>
{
	void accept(T target, byte value);
}
