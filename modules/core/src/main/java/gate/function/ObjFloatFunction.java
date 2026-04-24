package gate.function;

@FunctionalInterface
public interface ObjFloatFunction<T, R>
{
	R apply(T target, float value);
}
