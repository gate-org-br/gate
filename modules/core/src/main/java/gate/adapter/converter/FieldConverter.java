package gate.adapter.converter;

import gate.annotation.Description;
import gate.error.ConversionException;
import gate.type.Field;

import java.lang.reflect.Type;


@Description("Campos do tipo Field devem estar no formado JSON.")
public class FieldConverter extends ObjectConverter
{
	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return Field.valueOf(string);
	}

}