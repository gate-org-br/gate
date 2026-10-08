package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fetches the first row as a map whose keys are the column names and values are the column values.
 */
public class MapFetcher implements Fetcher<Optional<Map<String, Object>>> {

	/**
	 * Fetches the first row as a map.
	 *
	 * @param cursor cursor to be fetched
	 * @return optional containing the first row as a map, or empty if the cursor has no rows
	 */
	@Override
	public Optional<Map<String, Object>> fetch(Cursor cursor) {
		if (!cursor.next())
			return Optional.empty();

		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, Class<?>> column : cursor.getMetaData().entrySet())
			result.put(column.getKey(), cursor.getValue(column.getValue(), column.getKey()));
		return Optional.of(result);
	}
}
