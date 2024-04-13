package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.util.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypedMapPageFetcher implements Fetcher<Page<Map<String, Object>>>
{

	private final int pageSize;
	private final int pageIndx;
	private final Class<?>[] types;

	public TypedMapPageFetcher(int pageSize, int pageIndx, Class<?>... types)
	{
		this.types = types;
		this.pageSize = pageSize;
		this.pageIndx = pageIndx;
	}

	@Override
	public Page<Map<String, Object>> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Page.of(List.of(), 0, pageSize, pageIndx);

		List<String> names = cursor.getColumnNames();

		if (names.stream().noneMatch("dataSize"::equals))
			throw new UnsupportedOperationException(
					"Result set does not contain a dataSize column");

		int dataSize = cursor.getIntValue("dataSize");

		List<Map<String, Object>> results = new ArrayList<>();
		do
		{
			Map<String, Object> result = new HashMap<>();
			for (int i = 0; i < names.size(); i++)
			{
				String name = names.get(i);
				if (!"dataSize".equals(name))
					result.put(name, cursor.getValue(types[i], name));
			}
			results.add(result);
		} while (cursor.next());
		return Page.of(results, dataSize, pageSize, pageIndx);
	}
}
