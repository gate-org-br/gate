package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			Map<String, Object> result = new HashMap<>();
			for (int i = 0; i < names.size(); i++)
				result.put(names.get(i), rs.getValue(types[i], names.get(i)));
			results.add(result);
		}
		return results;
	}
}
