package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches all rows as maps whose keys are the column names and values are the column values.
 */
public class MapListFetcher implements Fetcher<List<Map<String, Object>>>
{

	@Override
	public List<Map<String, Object>> fetch(Cursor rs)
	{
		Map<String, Class<?>> metaData = rs.getMetaData();
		List<Map<String, Object>> results = new ArrayList<>();
		while (rs.next())
		{
			Map<String, Object> result = new LinkedHashMap<>();
			for (Map.Entry<String, Class<?>> column : metaData.entrySet())
				result.put(column.getKey(), rs.getValue(column.getValue(), column.getKey()));
			results.add(result);
		}
		return results;
	}
}
