package gate.stream;

@FunctionalInterface
public interface CheckedConsumer<T>
{

	void accept(T object) throws Exception;
}
