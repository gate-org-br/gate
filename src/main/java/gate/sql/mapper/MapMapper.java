package gate.sql.mapper;

import gate.sql.Cursor;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Extracts each row from a Cursor as stream of maps whose keys are the column
 * names and values are the column values.
 */
public class MapMapper implements Mapper<Map<String, Object>>
{

	/**
	 * Extract each row of the specified cursor as stream of maps whose keys
	 * are the column names and values are the column values.
	 *
	 * @param cursor the cursor from where to extract the values
	 *
	 * @return each row of the specified cursor as stream of maps whose keys
	 * are the column names and values are the column values
	 */
	@Override
	public Map<String, Object> apply(Cursor cursor)
	{
		return cursor.getMetaData()
			.entrySet().stream()
			.collect(Collectors.toMap(e -> e.getKey(), e -> cursor.getValue(e.getValue(), e.getKey())));
	}
}
