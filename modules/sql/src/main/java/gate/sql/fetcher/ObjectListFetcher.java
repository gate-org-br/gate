package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches the first column of all rows as a list of Java objects.
 */
public class ObjectListFetcher implements Fetcher<List<Object>>
{

	private final List<Object> result = new ArrayList<>();

	/**
	 * Fetches the first column of all rows.
	 *
	 * @param cursor cursor to be fetched
	 * @return list of fetched values
	 */
	@Override
	public List<Object> fetch(Cursor cursor)
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
	public List<Object> getResult()
	{
		return result;
	}
}
