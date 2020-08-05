package gate.stream;

@FunctionalInterface
public interface CheckedSupplier<T>
{

	public T get() throws Exception;

}
