package gate.policonverter;

import gate.error.ConversionException;
import gate.converter.Converter;

import java.lang.reflect.Array;
import javax.servlet.http.Part;

public class ArrayPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value) throws ConversionException
	{
		Object[] objects = (Object[]) Array.newInstance(type, value.length);
		for (int i = 0; i < value.length; i++)
			objects[i] = Converter.getConverter(type).ofString(type, value[i]);
		return objects;
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		Object[] objects = (Object[]) Array.newInstance(type, value.length);
		for (int i = 0; i < value.length; i++)
			objects[i] = Converter.getConverter(type).ofPart(type, value[i]);
		return objects;
	}

	@Override
	public String[] getString(Class<?> type, Object value) throws ConversionException
	{
		Object[] objects = (Object[]) value;
		String[] strings = new String[objects.length];
		for (int i = 0; i < strings.length; i++)
			strings[i] = Converter.getConverter(type).toString(type, objects[i]);
		return strings;
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		return objects;
	}
}
