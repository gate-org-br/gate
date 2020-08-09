package gate.stream;

@FunctionalInterface
public interface CheckedConsumer<T>
{

	public void accept(T object) throws Exception;
}
