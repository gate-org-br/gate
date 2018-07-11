package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a Cursor as a list of java arrays or the specified types.
 */
public class TypedArrayListFetcher implements Fetcher<List<Object[]>>
{

	private final Class[] types;

	/**
	 * Creates a new TypedArrayListFetcher with the specified types.
	 *
	 * @param types an array specifying the types of the objects to be fetched
	 */
	public TypedArrayListFetcher(Class[] types)
	{
		this.types = types;
	}

	/**
	 * Fetches each row as a list of java arrays of Objects of the specified types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a list of java arrays of Objects of the specified types
	 *
	 * @throws SQLException if a SQLException is thrown while fetching the results
	 * @throws ConversionException if the result cannot be converted to a list of java array
	 */
	@Override
	public List<Object[]> fetch(Cursor cursor) throws SQLException, ConversionException
	{
		List<Object[]> results = new ArrayList<>();
		while (cursor.next())
		{
			Object[] result = new Object[types.length];
			for (int i = 0; i < result.length; i++)
				result[i] = cursor.getCurrentValue(types[i]);
			results.add(result);
		}
		return results;
	}
}
