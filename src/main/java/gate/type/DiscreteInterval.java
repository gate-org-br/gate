package gate.type;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DiscreteInterval<T extends Comparable<? super T>> extends Interval<T>, Iterable<T>
{

	/**
	 * Returns the number of values in this range.
	 *
	 * @return the number of values in this range
	 */
	long size();

	/**
	 * Returns a sequential Stream with all values in this range.
	 *
	 * @return a sequential Stream with all values in this range
	 */
	Stream<T> stream();

	/**
	 * Returns a list with all values in this range.
	 *
	 * @return a list with all values in this range
	 */
	default List<T> toList()
	{
		return stream().collect(Collectors.toList());
	}
}
