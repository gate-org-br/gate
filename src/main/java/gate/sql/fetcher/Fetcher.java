package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;

/**
 * Fetches a cursor as a java object of the specified type.
 *
 * @author Davi Nunes da Silva
 * @param <T> type returned by the Fetcher
 */
public interface Fetcher<T>
{

	/**
	 * Fetches a cursor as a java object of the specified type.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return the results as a object of the specified type
	 * @throws SQLException if a SQLException is thrown while fetching the results
	 * @throws ConversionException if the result cannot be converted to an object of the specified type
	 */
	public T fetch(Cursor cursor) throws SQLException, ConversionException;
}
