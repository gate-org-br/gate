package gate.stream;

@FunctionalInterface
public interface CheckedFunction<T, R>
{

	public R apply(T t) throws Exception;

}
