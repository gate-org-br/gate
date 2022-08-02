package gate.sql.mapper;

import gate.sql.Cursor;
import java.util.stream.Stream;

/**
 * Extracts each row from a Cursor as stream of arrays.
 */
public class TypedArrayMapper implements Mapper<Object[]>
{

	private final Class<?>[] types;

	public TypedArrayMapper(Class<?>[] types)
	{
		this.types = types;
	}

	/**
	 * Extract each row of the specified cursor as stream of arrays of
	 * objects of the specified types.
	 *
	 * @param cursor the cursor from where to extract the values
	 *
	 * @return each row of the specified cursor as stream of arrays of
	 * objects of the specified types
	 */
	@Override
	public Object[] apply(Cursor cursor)
	{
		return Stream.of(types)
			.map(e -> cursor.getCurrentValue(e))
			.toArray();
	}
}
