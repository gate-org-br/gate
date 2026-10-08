package gate.function;

@FunctionalInterface
public interface ObjShortConsumer<T>
{
	void accept(T target, short value);
}
