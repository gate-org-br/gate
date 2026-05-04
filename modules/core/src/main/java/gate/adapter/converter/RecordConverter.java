package gate.adapter.converter;

import gate.adapter.jsonConverter.JsonConverter;
import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonElement;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class RecordConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		var bytes = Base64.getDecoder().decode(string);
		string = new String(bytes, StandardCharsets.UTF_8);
		return JsonConverter.fromJson(type, JsonElement.parse(string));
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		var element = JsonConverter.toJson(object);
		var bytes = element.toString().getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(bytes);
	}
}