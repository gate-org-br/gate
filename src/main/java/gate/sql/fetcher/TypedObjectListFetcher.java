package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list of java objects of the specified types.
 *
 * 
 */
public class TypedObjectListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final List<T> result = new ArrayList<>();

	public TypedObjectListFetcher(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Fetches each row as a list of java arrays of Objects of the specified
	 * types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a list of java Objects of the specified
	 * types
	 */
	@Override
	public List<T> fetch(Cursor cursor)
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
	public List<T> getResult()
	{
		return result;
	}
}
