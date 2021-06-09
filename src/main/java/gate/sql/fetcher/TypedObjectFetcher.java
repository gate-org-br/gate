package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Fetches a Cursor as a object of the specified type.
 *
 * 
 */
public class TypedObjectFetcher<T> implements Fetcher<Optional<T>>
{

	private final Class<T> type;

	/**
	 * Creates a new TypedObjectFetcher with the specified type.
	 *
	 * @param type the type of the object to be fetched
	 */
	public TypedObjectFetcher(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Fetches the first column of the first row as a java object of the specified type.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return an Optional describing the first column of the first row as a java object of the specified type or an
	 * empty Optional if the result is empty
	 *
	 */
	@Override
	public Optional<T> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		return Optional.ofNullable(cursor.getValue(type, 1));
	}
}
