package gate.function;

@FunctionalInterface
public interface ObjBooleanConsumer<T>
{
	void accept(T target, boolean value);
}
