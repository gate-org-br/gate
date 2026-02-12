package gate.cache;

@FunctionalInterface
public interface Generator<T>
{

	T get() throws Exception;
}
