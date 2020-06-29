package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list of java objects.
 */
public class ObjectListFetcher implements Fetcher<List<Object>>
{

	private final List<Object> result = new ArrayList<>();

	/**
	 * Fetches each row as a list of java arrays of Objects.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a list of java Objects
	 */
	@Override
	public List<Object> fetch(Cursor cursor)
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
	public List<Object> getResult()
	{
		return result;
	}
}
