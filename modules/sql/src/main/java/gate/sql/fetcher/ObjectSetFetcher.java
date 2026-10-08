package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches the first column of all rows as a set of Java objects.
 */
public class ObjectSetFetcher implements Fetcher<Set<Object>>
{

	private final Set<Object> result = new LinkedHashSet<>();

	/**
	 * Fetches the first column of all rows.
	 *
	 * @param cursor cursor to be fetched
	 * @return set of fetched values
	 */
	@Override
	public Set<Object> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(1));
		return result;
	}

	/**
	 * Returns the accumulated result of fetch operations.
	 *
	 * @return accumulated result
	 */
	public Set<Object> getResult()
	{
		return result;
	}
}
