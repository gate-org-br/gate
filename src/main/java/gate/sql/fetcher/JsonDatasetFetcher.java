package gate.sql.fetcher;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;
import gate.sql.Cursor;
import java.util.stream.Collectors;

/**
 * Fetches a cursor as a bidirectional JsonArray.
 */
public class JsonDatasetFetcher implements Fetcher<JsonArray>
{

	private final boolean includeHeader;

	/**
	 * Creates a new JsonDatasetFetcher.
	 *
	 * @param includeHeader define if the column headers are to be included on the result
	 */
	public JsonDatasetFetcher(boolean includeHeader)
	{
		this.includeHeader = includeHeader;
	}

	@Override
	public JsonArray fetch(Cursor cursor)
	{
		JsonArray results = new JsonArray();

		if (includeHeader)
			results.add(cursor.getColumnNames().stream().map(JsonString::of)
				.collect(Collectors.toCollection(JsonArray::new)));

		while (cursor.next())
			results.add(cursor.getColumnValues().stream().map(JsonElement::of)
				.collect(Collectors.toCollection(JsonArray::new)));

		return results;
	}
}
