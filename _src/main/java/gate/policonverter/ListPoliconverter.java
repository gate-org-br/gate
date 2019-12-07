package gate.policonverter;

import gate.converter.Converter;
import gate.error.ConversionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Part;

public class ListPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
		throws ConversionException
	{
		List<Object> objects = new ArrayList<>();
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
		List<String> strings = new ArrayList<>();
		for (Object object : (List<?>) value)
			strings.add(Converter.getConverter(type).toString(type, object));
		return strings.toArray(new String[0]);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		Collection<Object> result = new ArrayList<Object>();
		Collections.addAll(result, objects);
		return result;
	}
}
