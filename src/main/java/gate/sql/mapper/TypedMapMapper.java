package gate.sql.mapper;

import gate.sql.Cursor;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts each row from a Cursor as stream of maps whose keys are the column
 * names and values are the column values.
 */
public class TypedMapMapper implements Mapper<Map<String, Object>>
{

	private final Class[] types;

	public TypedMapMapper(Class[] types)
	{
		this.types = types;
	}

	/**
	 * Extract each row of the specified cursor as stream of maps whose keys
	 * are the column names and values are the column values as objects of
	 * the specified types.
	 *
	 * @param cursor the cursor from where to extract the values
	 *
	 * @return each row of the specified cursor as stream of maps whose keys
	 * are the column names and values are the column values as objects of
	 * the specified types
	 */
	@Override
	public Map<String, Object> apply(Cursor cursor)
	{
		String[] names = cursor.getColumnNames();
		Map<String, Object> result = new HashMap<>();
		for (int i = 0; i < names.length; i++)
			result.put(names[i], cursor.getValue(types[i], names[i]));
		return result;
	}
}
