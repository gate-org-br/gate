package gate.function;

@FunctionalInterface
public interface ObjIntFunction<T, R>
{
	R apply(T target, int value);
}
