package gate.type;

public interface Interval<T>
{

	public T getMin();

	public T getMax();

	public boolean contains(T value);
}
