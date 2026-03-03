package gate.function;

@FunctionalInterface
public interface ObjBooleanFunction<T, R>
{
	R apply(T target, boolean value);
}
