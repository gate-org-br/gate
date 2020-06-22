package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Fetches the first row of a Cursor as a java array or the specified types.
 */
public class TypedArrayFetcher implements Fetcher<Optional<Object[]>>
{

	private final Class[] types;

	/**
	 * Creates a new TypedArrayFetcher with the specified types.
	 *
	 * @param types an array specifying the types of the objects to be fetched
	 */
	public TypedArrayFetcher(Class[] types)
	{
		this.types = types;
	}

	/**
	 * Fetches each row of the specified cursor as a list of java arrays of Objects of the specified types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return each row fetched as a list of java arrays of Objects of the specified types
	 *
	 */
	@Override
	public Optional<Object[]> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		Object[] result = new Object[types.length];
		for (int i = 0; i < result.length; i++)
			result[i] = cursor.getCurrentValue(types[i]);
		return Optional.of(result);
	}
}
