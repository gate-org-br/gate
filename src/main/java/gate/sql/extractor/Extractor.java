package gate.sql.extractor;

import gate.sql.Cursor;
import java.util.stream.Stream;

/**
 * Extracts the rows from a cursor as a stream of java objects of the specified type.
 *
 * 
 */
public interface Extractor<T>
{

	/**
	 * Extracts rows from a cursor as a stream of java objects of the specified type.
	 *
	 * @param cursor the cursor from where to extract the stream of java objects
	 *
	 * @return a stream of java of the specified type objects extracted from the specified cursor
	 */
	public Stream<T> extract(Cursor cursor);
}
