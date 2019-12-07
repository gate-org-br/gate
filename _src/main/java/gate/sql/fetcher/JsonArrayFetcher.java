package gate.sql.fetcher;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.sql.Cursor;
import java.util.Map;

/**
 * Fetches a cursor as a JsonArray whose keys are the column names and values their respective column values.
 */
public class JsonArrayFetcher implements Fetcher<JsonArray>
{

	@Override
	public JsonArray fetch(Cursor cursor)
	{
		JsonArray results = new JsonArray();
		Map<String, Class<?>> metaData = cursor.getMetaData();

		while (cursor.next())
		{
			JsonObject result = new JsonObject();
			metaData.entrySet().forEach(column -> result.put(column.getKey(),
				JsonElement.of(cursor.getValue(column.getValue(), column.getKey()))));
			results.add(result);
		}
		return results;
	}
}
