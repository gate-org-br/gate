package gate.stream;

@FunctionalInterface
public interface CheckedSupplier<T>
{

	T get() throws Exception;

}
