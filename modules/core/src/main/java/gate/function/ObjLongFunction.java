package gate.function;

@FunctionalInterface
public interface ObjLongFunction<T, R>
{
	R apply(T target, long value);
}
