package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches the first column of all rows as a set of objects of the specified type.
 *
 * @param <T> target type
 */
public class TypedObjectSetFetcher<T> implements Fetcher<Set<T>>
{

	private final Class<T> type;
	private final Set<T> result = new LinkedHashSet<>();

	public TypedObjectSetFetcher(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Fetches the first column of all rows as the requested type.
	 *
	 * @param cursor cursor to be fetched
	 * @return set of fetched values converted to the requested type
	 */
	@Override
	public Set<T> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(type, 1));
		return result;
	}

	/**
	 * Returns the accumulated result of fetch operations.
	 *
	 * @return accumulated result
	 */
	public Set<T> getResult()
	{
		return result;
	}
}
