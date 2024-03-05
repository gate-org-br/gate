package gate.converter;

import gate.error.ConversionException;
import gate.lang.json.JsonWriter;

public class DataGridConverter extends ObjectConverter
{

	@Override
	public <T> void toJson(JsonWriter writer, Class<T> type, T object) throws ConversionException
	{

		writer.write(object != null ? object.toString() : "null");
	}
}
