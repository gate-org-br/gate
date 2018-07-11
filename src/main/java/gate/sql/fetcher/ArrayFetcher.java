package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Fetches the first row of a Cursor as a java array.
 *
 * @author Davi Nunes da Silva
 */
public class ArrayFetcher implements Fetcher<Optional<Object[]>>
{

	/**
	 * Fetches the first row of a Cursor as a java array.
	 *
	 * @param cursor the Cursor to be fetched
	 *
	 * @return an Optional describing the first row of the Cursor as a java array, or an empty Optional if the
	 *         cursor is empty
	 *
	 * @throws SQLException        if a SQLException is thrown while fetching the results
	 * @throws ConversionException if the result cannot be converted to a java array
	 */
	@Override
	public Optional<Object[]> fetch(Cursor cursor) throws SQLException, ConversionException
	{
		if (!cursor.next())
			return Optional.empty();

		Class<?>[] types = cursor.getColumnTypes();
		Object[] result = new Object[types.length];
		for (int i = 0; i < types.length; i++)
			result[i] = cursor.getCurrentValue(types[i]);
		return Optional.of(result);
	}
}
