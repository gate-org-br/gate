package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a cursor as a list of java arrays.
 */
public class ArrayListFetcher implements Fetcher<List<Object[]>>
{

	@Override
	public List<Object[]> fetch(Cursor rs)
	{
		Class[] types = rs.getColumnTypes();
		List<Object[]> results = new ArrayList<>();
		while (rs.next())
		{
			Object[] result = new Object[types.length];
			for (int i = 0; i < result.length; i++)
				result[i] = rs.getCurrentValue(types[i]);
			results.add(result);
		}
		return results;
	}
}
