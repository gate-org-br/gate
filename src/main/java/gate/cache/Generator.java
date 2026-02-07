package gate.cache;

@FunctionalInterface
public interface Generator<T>
{

	public T get() throws Exception;
}
