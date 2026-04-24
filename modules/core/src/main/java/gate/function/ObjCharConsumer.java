package gate.function;

@FunctionalInterface
public interface ObjCharConsumer<T>
{
	void accept(T target, char value);
}
