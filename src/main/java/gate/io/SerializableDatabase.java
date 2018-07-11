package gate.io;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class SerializableDatabase<T extends Serializable> extends AbstractDatabase<T>
{

	SerializableDatabase(Class<T> type, File folder, Map<String, Table<T>> tables)
	{
		super(type, folder, tables);
	}

	@Override
	public Table create(String fileName)
	{
		return SerializableTable.create(type, new File(folder, fileName));
	}

	static <T extends Serializable> SerializableDatabase<T> create(Class<T> type, File folder)
	{
		Map<String, Table> tables = new HashMap<>();
		File[] files = folder.listFiles();
		if (files != null)
			Stream.of(files).forEach(e -> tables.put(e.getName(),
				SerializableTable.create(type, e)));
		return new SerializableDatabase(type, folder, tables);
	}
}
