package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fetches the first row as a map whose keys are the column names and values are the column values.
 */
public class MapFetcher implements Fetcher<Optional<Map<String, Object>>>
{

	/**
	 * Fetches the first row as a map whose keys are the column names and values are the column values.
	 *
	 * @return an Optional describing the first row as a map whose keys are the column names and values are the
	 * column values or an empty Optional if the result is empty
	 *
	 */
	@Override
	public Optional<Map<String, Object>> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();

		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, Class<?>> column : cursor.getMetaData().entrySet())
			result.put(column.getKey(), cursor.getValue(column.getValue(), column.getKey()));
		return Optional.of(result);
	}
}
