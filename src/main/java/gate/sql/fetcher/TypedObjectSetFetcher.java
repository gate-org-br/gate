package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

public class TypedObjectSetFetcher<T> implements Fetcher<Set<T>>
{

	private final Class<T> type;
	private final Set<T> result = new LinkedHashSet<>();

	public TypedObjectSetFetcher(Class<T> type)
	{
		this.type = type;
	}

	@Override
	public Set<T> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(type, 1));
		return result;
	}

	public Set<T> getResult()
	{
		return result;
	}
}
