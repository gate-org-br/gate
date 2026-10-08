package gate.adapter.jsonConverter;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.type.DataGrid;

import java.lang.reflect.Type;

public class DataGridJsonConverter implements JsonConverter
{
	@Override
	public Object ofJson(Type genericType, JsonElement element)
	{
		if (!(element instanceof JsonArray jsonArray) || jsonArray.isEmpty())
			return null;

		String[] head = jsonArray.get(0) instanceof JsonArray array
				? array.stream().map(JsonElement::unwrap).map(String::valueOf).toArray(String[]::new)
				: new String[0];

		DataGrid result = new DataGrid(head);
		for (int i = 1; i < jsonArray.size(); i++)
			if (jsonArray.get(i) instanceof JsonArray row)
				result.add(row.stream().map(JsonElement::unwrap).toArray());
		return result;
	}

	@Override public JsonElement toJson(Class<?> type, Object object)
	{
		return object != null ? ((DataGrid) object).toJson() : null;
	}
}
