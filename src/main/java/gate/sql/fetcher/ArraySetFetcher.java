package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches a cursor as a set of java arrays.
 */
public class ArraySetFetcher implements Fetcher<Set<Object[]>>
{

	private final Set<Object[]> result = new LinkedHashSet<>();

	/**
	 * Fetches each row as a set of java arrays of Objects.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a set of java arrays of Objects
	 */
	@Override
	public Set<Object[]> fetch(Cursor cursor)
	{
		Class[] types = cursor.getColumnTypes();
		while (cursor.next())
		{
			Object[] array = new Object[types.length];
			for (int i = 0; i < array.length; i++)
				array[i] = cursor.getCurrentValue(types[i]);
			result.add(array);
		}
		return result;
	}

	/**
	 * Return the accumulated result of fetch operations.
	 *
	 * @return the accumulated result of fetch operations
	 */
	public Set<Object[]> getResult()
	{
		return result;
	}
}
