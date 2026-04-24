package gate.function;

@FunctionalInterface
public interface ObjShortFunction<T, R>
{
	R apply(T target, short value);
}
