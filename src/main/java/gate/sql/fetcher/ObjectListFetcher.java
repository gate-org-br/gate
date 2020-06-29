package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list list of java objects.
 */
public class ObjectListFetcher implements Fetcher<List<Object>>
{

	private final List<Object> result = new ArrayList<>();

	@Override
	public List<Object> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(1));
		return result;
	}

	public List<Object> getResult()
	{
		return result;
	}
}
