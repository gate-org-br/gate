package gate.function;

@FunctionalInterface
public interface ObjByteFunction<T, R>
{
	R apply(T target, byte value);
}
