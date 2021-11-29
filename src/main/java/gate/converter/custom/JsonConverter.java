package gate.converter.custom;

import gate.converter.*;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;

public class JsonConverter extends ObjectConverter
{

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null || string.isBlank())
			return null;

		try ( JsonScanner scanner = new JsonScanner(new StringReader(string)))
		{
			return ofJson(scanner, type, type);
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";

		try ( StringWriter string = new StringWriter();
			 JsonWriter writer = new JsonWriter(string))
		{
			toJson(writer, (Class<Object>) type, object);
			return string.toString();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
