package gate.sql.fetcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gate.sql.Cursor;

public class TypedMapListFetcher implements Fetcher<List<Map<String, Object>>>
{

	private final Class<?>[] types;

	public TypedMapListFetcher(Class<?>[] types)
	{
		this.types = types;
	}

	@Override
	public List<Map<String, Object>> fetch(Cursor rs)
	{

		List<String> names = rs.getColumnNames();
		List<Map<String, Object>> results = new ArrayList<>();
		while (rs.next())
		{
			Map<String, Object> result = new LinkedHashMap<>();
			for (int i = 0; i < names.size(); i++)
				result.put(names.get(i), rs.getValue(types[i], names.get(i)));
			results.add(result);
		}
		return results;
	}
}
