package gate.adapter.converter;

import gate.error.ConversionException;
import gate.lang.json.JsonWriter;

import java.util.Deque;

public class DataGridConverter extends ObjectConverter
{

	@Override
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{

		writer.write(object != null ? object.toString() : "null");
	}
}