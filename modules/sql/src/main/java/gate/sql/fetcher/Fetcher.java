package gate.sql.fetcher;

import gate.sql.Cursor;

/**
 * Consumes a {@link Cursor} and returns a result object.
 *
 * @param <T> result type produced by the fetcher
 */
public interface Fetcher<T>
{

	/**
	 * Consumes the given cursor.
	 *
	 * @param cursor cursor to consume
	 * @return fetched result
	 */
	T fetch(Cursor cursor);
}
