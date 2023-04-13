package gate.sql.mapper;

import gate.sql.Cursor;

/**
 * Map the first column of cursor to a java object.
 */
public class ObjectMapper implements Mapper<Object>
{

	/**
	 * Extract each row of the specified cursor as stream of java objects.
	 *
	 * @param cursor the cursor from where to extract the values
	 *
	 * @return each row of the specified cursor as stream of java objects
	 */
	@Override
	public Object apply(Cursor cursor)
	{
		return cursor.getValue(cursor.getColumnTypes()[0], 1);
	}
}
