package gate.sql.mapper;

import gate.sql.Cursor;

/**
 * Extracts each row from a Cursor as a stream of java objects of the specified
 * type with their properties set to their respective column values.
 */
public class EntityMapper<T> implements Mapper<T>
{

	private final Class<T> type;

	/**
	 * Creates a new EntityExtractor for the specified java type.
	 *
	 * @param type the java type of the objects to be extracted
	 */
	public EntityMapper(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Extracts each row from the specified Cursor as a stream of java
	 * objects of the specified type with their properties set to their
	 * respective column values.
	 *
	 * @param cursor the Cursor from with the objects will be extracted
	 *
	 * @return a stream of java objects of the specified type with their
	 * properties set to their respective column values
	 */
	@Override
	public T apply(Cursor cursor)
	{
		return cursor.getEntity(type);
	}
}
