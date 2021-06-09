package gate.sql.fetcher;

import gate.sql.Cursor;

/**
 * Fetches a cursor as a java object of the specified type.
 *
 * @author Davi Nunes da Silva
 * 
 */
public interface Fetcher<T>
{

	/**
	 * Fetches a cursor as a java object of the specified type.
	 *
	 * @param cursor the cursor to be fetched
	 *
	 * @return the results as a object of the specified type
	 */
	T fetch(Cursor cursor);
}
