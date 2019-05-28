package gate.type;

public interface Interval<T extends Comparable<? super T>>
{
	
	public T getMin();
	
	public T getMax();
	
	default public boolean contains(T value)
	{
		return getMin().compareTo(value) <= 0 && getMax().compareTo(value) >= 0;
	}
	
	default public boolean contains(Interval<T> value)
	{
		return contains(value.getMin()) && contains(value.getMax());
	}
}
