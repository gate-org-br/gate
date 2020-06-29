package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fetches a Cursor as a set of java arrays or the specified types.
 */
public class TypedArraySetFetcher implements Fetcher<Set<Object[]>>
{

	private final Class[] types;
	private final Set<Object[]> result = new LinkedHashSet<>();

	/**
	 * Creates a new TypedArrayListFetcher with the specified types.
	 *
	 * @param types an array specifying the types of the objects to be
	 * fetched
	 */
	public TypedArraySetFetcher(Class[] types)
	{
		this.types = types;
	}

	/**
	 * Fetches each row as a set of java arrays of Objects of the specified
	 * types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a set of java arrays of Objects of the
	 * specified types
	 */
	@Override
	public Set<Object[]> fetch(Cursor cursor)
	{
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
