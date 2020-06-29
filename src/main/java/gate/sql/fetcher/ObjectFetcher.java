package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.Optional;

/**
 * Fetches the first column of the first row from a Cursor as a java object.
 *
 * @author davins
 */
public class ObjectFetcher implements Fetcher<Optional<Object>>
{

	/**
	 * Fetches the first column of the first row of the specified Cursor as a java object.
	 *
	 * @param cursor Cursor from where to fetch the result
	 *
	 * @return an Optional containing the first column of the first row of the specified Cursor as a java object or a
	 *         empty optional if the Cursor is empty
	 *
	 */
	@Override
	public Optional<Object> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();
		return Optional.ofNullable(cursor.getValue(1));
	}
}
