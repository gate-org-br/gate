package gate.sql;

import java.util.function.Consumer;

public interface Thenable<T, R>
{

	public R then(Consumer<T> consumer);
}
