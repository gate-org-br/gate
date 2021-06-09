package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches a cursor as a set of java objects of the specified types.
 * 
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
	 * Fetches each row as a set of java arrays of Objects of the specified
	 * types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a set of java Objects of the specified
	 * types
	 */
	@Override
	public Set<T> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(type, 1));
		return result;
	}

	/**
	 * Return the accumulated result of fetch operations.
	 *
	 * @return the accumulated result of fetch operations
	 */
	public Set<T> getResult()
	{
		return result;
	}
}
