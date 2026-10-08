package gate.function;

@FunctionalInterface
public interface ObjCharFunction<T, R>
{
	R apply(T target, char value);
}
