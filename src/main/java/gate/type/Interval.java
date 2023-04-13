package gate.type;

public interface Interval<T extends Comparable<? super T>>
{

	T getMin();

	T getMax();

	default boolean contains(T value)
	{
		return getMin().compareTo(value) <= 0
			&& getMax().compareTo(value) >= 0;
	}

	default boolean containsProperly(T value)
	{
		return getMin().compareTo(value) < 0
			&& getMax().compareTo(value) > 0;
	}

	default boolean contains(Interval<T> value)
	{
		return contains(value.getMin())
			&& contains(value.getMax());
	}

	default boolean containsProperly(Interval<T> value)
	{
		return containsProperly(value.getMin())
			&& containsProperly(value.getMax());
	}

	default boolean intersects(Interval<T> interval)
	{
		return contains(interval.getMin())
			|| contains(interval.getMax())
			|| interval.contains(getMin())
			|| interval.contains(getMax());
	}

	default boolean intersectsProperly(Interval<T> interval)
	{
		return containsProperly(interval.getMin())
			|| containsProperly(interval.getMax())
			|| interval.containsProperly(getMin())
			|| interval.containsProperly(getMax());
	}

}
