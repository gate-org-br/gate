package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a Cursor as a list of java arrays or the specified types.
 */
public class TypedArrayListFetcher implements Fetcher<List<Object[]>>
{

	private final Class<?>[] types;
	private final List<Object[]> result = new ArrayList<>();

	/**
	 * Creates a new TypedArrayListFetcher with the specified types.
	 *
	 * @param types an array specifying the types of the objects to be fetched
	 */
	public TypedArrayListFetcher(Class<?>[] types)
	{
		this.types = types;
	}

	/**
	 * Fetches each row as a list of java arrays of Objects of the specified types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a list of java arrays of Objects of the specified types
	 */
	@Override
	public List<Object[]> fetch(Cursor cursor)
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
	public List<Object[]> getResult()
	{
		return result;
	}
}
