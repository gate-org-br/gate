package gate.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class JsonDatabase<T> extends AbstractDatabase<T>
{

	JsonDatabase(Class<T> type, File folder, Map<String, Table<T>> tables)
	{
		super(type, folder, tables);
	}

	@Override
	public Table create(String fileName)
	{
		return JsonTable.create(type, new File(folder, fileName));
	}

	static <T> JsonDatabase<T> create(Class<T> type, File folder)
	{
		Map<String, Table> tables = new HashMap<>();
		File[] files = folder.listFiles();
		if (files != null)
			Stream.of(files).forEach(e -> tables.put(e.getName(),
				JsonTable.create(type, e)));
		return new JsonDatabase(type, folder, tables);
	}
}
