package gate.stream;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<T>
{

	public void accept(T object) throws Exception;
}
