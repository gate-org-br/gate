package gate.function;

@FunctionalInterface
public interface ObjFloatConsumer<T>
{
	void accept(T target, float value);
}
