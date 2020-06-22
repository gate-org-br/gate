package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fetches the first row of a Cursor as a map whose keys are the column names and values are the column values as
 * objects of the specified types.
 */
public class TypedMapFetcher implements Fetcher<Optional<Map<String, Object>>>
{

	private final Class[] types;

	/**
	 * Creates a new TypedMapFetcher with the specified types.
	 *
	 * @param types an array specifying the types of the objects to be fetched
	 */
	public TypedMapFetcher(Class[] types)
	{
		this.types = types;
	}

	/**
	 * Fetches the first row as a map whose keys are the column names and values are the column values as objects of
	 * the specified types.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return an Optional describing the first row as a map whose keys are the column names and values are the
	 * column values as objects of the specified type or an empty Optional if the result is empty
	 *
	 */
	@Override
	public Optional<Map<String, Object>> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		String[] names = cursor.getColumnNames();
		Map<String, Object> result = new HashMap<>();

		for (int i = 0; i < names.length; i++)
			result.put(names[i], cursor.getValue(types[i], names[i]));
		return Optional.of(result);
	}
}
