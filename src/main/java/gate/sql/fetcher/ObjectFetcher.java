package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.Optional;

/**
 * Fetches the first column of the first row as a Java object.
 */
public class ObjectFetcher implements Fetcher<Optional<Object>>
{

	/**
	 * Fetches the first column of the first row.
	 *
	 * @param cursor cursor to be fetched
	 * @return optional containing the first value, or empty if the cursor has no rows
	 */
	@Override
	public Optional<Object> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		return Optional.ofNullable(cursor.getValue(1));
	}
}
