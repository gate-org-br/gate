package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches a cursor as a list list of java objects.
 */
public class ObjectSetFetcher implements Fetcher<Set<Object>>
{

	private final Set<Object> result = new LinkedHashSet<>();

	@Override
	public Set<Object> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(1));
		return result;
	}

	public Set<Object> getResult()
	{
		return result;
	}
}
