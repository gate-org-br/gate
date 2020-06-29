package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.List;

public class TypedObjectListFetcher<T> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final List<T> result = new ArrayList<>();

	public TypedObjectListFetcher(Class<T> type)
	{
		this.type = type;
	}

	@Override
	public List<T> fetch(Cursor cursor)
	{
		while (cursor.next())
			result.add(cursor.getValue(type, 1));
		return result;
	}

	public List<T> getResult()
	{
		return result;
	}
}
