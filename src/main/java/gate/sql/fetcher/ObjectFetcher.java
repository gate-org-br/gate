package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
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
	 * @throws java.sql.SQLException          if a SQLException is thrown when fetching results
	 * @throws gate.error.ConversionException if the result cannot be converted to a java object
	 */
	@Override
	public Optional<Object> fetch(Cursor cursor) throws SQLException, ConversionException
	{
		if (!cursor.next())
			return Optional.empty();
		return Optional.ofNullable(cursor.getValue(1));
	}
}
