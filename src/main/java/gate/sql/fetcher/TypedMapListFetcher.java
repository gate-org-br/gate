package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.error.ConversionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypedMapListFetcher implements Fetcher<List<Map<String, Object>>>
{

	private final Class[] types;

	public TypedMapListFetcher(Class[] types)
	{
		this.types = types;
	}

	@Override
	public List<Map<String, Object>> fetch(Cursor rs) throws SQLException, ConversionException
	{

		String[] names = rs.getColumnNames();
		List<Map<String, Object>> results = new ArrayList<>();
		while (rs.next())
		{
			Map<String, Object> result = new HashMap<>();
			for (int i = 0; i < names.length; i++)
				result.put(names[i], rs.getValue(types[i], names[i]));
			results.add(result);
		}
		return results;
	}
}
