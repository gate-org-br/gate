package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.util.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

		String[] names = cursor.getColumnNames();

		if (Stream.of(names).noneMatch("dataSize"::equals))
			throw new UnsupportedOperationException("Result set does not contain a dataSize column");

		int dataSize = cursor.getIntValue("dataSize");

		List<Map<String, Object>> results = new ArrayList<>();
		do
		{
			Map<String, Object> result = new HashMap<>();
			for (int i = 0; i < names.length; i++)
				if (!"dataSize".equals(names[i]))
					result.put(names[i], cursor.getValue(types[i], names[i]));
			results.add(result);
		} while (cursor.next());
		return Page.of(results, dataSize, pageSize, pageIndx);
	}
}
