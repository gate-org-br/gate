package gate.sql.fetcher;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.sql.Cursor;
import java.util.Optional;

/**
 * Fetches the first row as a JSON object whose keys are the column names and values are the column values.
 */
public class JsonObjectFetcher implements Fetcher<Optional<JsonObject>>
{

	/**
	 * Fetches the first row as a JSON object whose keys are the column names and values are the column values.
	 *
	 * @return an Optional describing the first row as a JSON object whose keys are the column names and values are the column values or an empty Optional if the
	 * result is empty
	 *
	 */
	@Override
	public Optional<JsonObject> fetch(Cursor cursor)
	{
		if (!cursor.next())
			return Optional.empty();

		JsonObject result = new JsonObject();
		cursor.getMetaData().entrySet().forEach(column -> result.put(column.getKey(),
			JsonElement.of(cursor.getValue(column.getValue(), column.getKey()))));
		return Optional.of(result);
	}
}
