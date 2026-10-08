package gate.function;

@FunctionalInterface
public interface ObjDoubleFunction<T, R>
{
	R apply(T target, double value);
}
