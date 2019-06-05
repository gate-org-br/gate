package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list list of java objects.
 */
public class ObjectListFetcher implements Fetcher<List<Object>>
{

	@Override
	public List<Object> fetch(Cursor rs)
	{
		List<Object> results = new ArrayList<>();
		while (rs.next())
			results.add(rs.getValue(1));
		return results;
	}
}
