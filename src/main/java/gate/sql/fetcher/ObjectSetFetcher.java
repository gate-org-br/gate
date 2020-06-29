package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches a cursor as a list of java objects.
 */
public class ObjectSetFetcher implements Fetcher<Set<Object>>
{

	private final Set<Object> result = new LinkedHashSet<>();

	/**
	 * Fetches each row as a set of java arrays of Objects.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a set of java Objects
	 */
	@Override
	public Set<Object> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(1));
		return result;
	}

	/**
	 * Return the accumulated result of fetch operations.
	 *
	 * @return the accumulated result of fetch operations
	 */
	public Set<Object> getResult()
	{
		return result;
	}
}
