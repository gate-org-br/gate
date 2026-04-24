package gate.sql.fetcher;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.sql.Cursor;
import java.util.stream.Collectors;

/**
 * Fetches a cursor as a JSON tree.
 */
public class JsonTreeFetcher implements Fetcher<JsonArray>
{

	@Override
	public JsonArray fetch(Cursor cursor)
	{
		JsonArray results = new JsonArray();

		while (cursor.next())
		{
			var label = cursor.getValue(String.class, "label");
			if (label == null)
				throw new UnsupportedOperationException("Column label cannot be null");

			var value = cursor.getValue(String.class, "value");
			if (value == null)
				throw new UnsupportedOperationException("Column value cannot be null");

			var parent = cursor.getValue(String.class, "parent");

			results.add(new JsonObject()
				.setString("parent", parent)
				.setString("value", value)
				.setString("label", label));
		}

		results.stream()
			.map(p -> (JsonObject) p)
			.forEach(p -> p.set("children",
			results
				.stream()
				.map(c -> (JsonObject) c)
				.filter(c -> p.get("value").equals(c.get("parent")))
				.collect(Collectors.toCollection(JsonArray::new))));

		results.removeIf(e -> ((JsonObject) e).containsKey("parent"));

		return results;
	}
}
