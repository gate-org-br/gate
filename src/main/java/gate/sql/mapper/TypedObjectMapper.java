package gate.sql.mapper;

import gate.sql.Cursor;

/**
 * Extracts each row from a Cursor as stream of java objects of the specified
 * type.
 */
public class TypedObjectMapper<T> implements Mapper<T>
{

	private final Class<T> type;

	public TypedObjectMapper(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Extract each row of the specified cursor as stream of java objects of
	 * the specified type.
	 *
	 * @param cursor the cursor from where to extract the values
	 *
	 * @return each row of the specified cursor as stream of java objects of
	 * the specified type
	 */
	@Override
	public T apply(Cursor cursor)
	{
		return cursor.getValue(type, 1);
	}
}
