package gate.sql.mapper;

import gate.sql.Cursor;
import java.util.stream.Stream;

/**
 * Extracts each row from a Cursor as stream of arrays.
 */
public class ArrayMapper implements Mapper<Object[]>
{

	/**
	 * Extract each row of the specified cursor as stream of arrays.
	 *
	 * @param cursor the cursor from where to extract the values
	 *
	 * @return each row of the specified cursor as stream of arrays
	 */
	@Override
	public Object[] apply(Cursor cursor)
	{
		return Stream.of(cursor.getColumnTypes())
			.map(e -> cursor.getCurrentValue(e))
			.toArray();
	}
}
