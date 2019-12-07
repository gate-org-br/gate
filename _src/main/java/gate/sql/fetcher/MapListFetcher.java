package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches a cursor as a list maps whose keys are the column names and values their respective column values.
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
			Map<String, Object> result = new HashMap<>();
			for (Map.Entry<String, Class<?>> column : metaData.entrySet())
				result.put(column.getKey(), rs.getValue(column.getValue(), column.getKey()));
			results.add(result);
		}
		return results;
	}
}
