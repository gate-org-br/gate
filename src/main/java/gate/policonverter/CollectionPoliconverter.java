package gate.policonverter;

import gate.error.ConversionException;
import gate.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.Part;

public class CollectionPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value) throws ConversionException
	{
		Collection<Object> objects = new ArrayList<>();
		for (String string : value)
			objects.add(Converter.getConverter(type).ofString(type, string));
		return objects;
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		Collection<Object> objects = new ArrayList<>();
		for (Part part : value)
			objects.add(Converter.getConverter(type).ofPart(type, part));
		return objects;
	}

	@Override
	public String[] getString(Class<?> type, Object value) throws ConversionException
	{
		Collection<String> strings = new ArrayList<>();
		for (Object object : (Collection<?>) value)
			strings.add(Converter.getConverter(type).toString(type, object));
		return strings.toArray(new String[strings.size()]);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		Collection<Object> result = new ArrayList<>();
		Collections.addAll(result, objects);
		return result;
	}
}
