package gate.policonverter;

import gate.converter.Converter;
import gate.error.ConversionException;
import jakarta.servlet.http.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CollectionPoliconverter implements Policonverter
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
	public String[] getString(Class<?> type, Object value)
	{
		Collection<String> strings = new ArrayList<>();
		for (Object object : (Collection<?>) value)
			strings.add(Converter.getConverter(type).toString(type, object));
		return strings.toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		Collection<Object> result = new ArrayList<>();
		Collections.addAll(result, objects);
		return result;
	}
}
