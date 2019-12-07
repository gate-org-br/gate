package gate.policonverter;

import gate.converter.Converter;
import gate.error.ConversionException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.Part;

public class SetPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
		throws ConversionException
	{
		Set<Object> objects = new HashSet<>();
		for (String string : value)
			objects.add(Converter.getConverter(type).ofString(type, string));
		return objects;
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		Collection<Object> objects = new HashSet<>();
		for (Part part : value)
			objects.add(Converter.getConverter(type).ofPart(type, part));
		return objects;
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		Set<String> strings = new HashSet<>();
		for (Object object : (Set<?>) value)
			strings.add(Converter.getConverter(type).toString(type, object));
		return strings.toArray(new String[0]);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		return new HashSet<>(Arrays.asList(objects));
	}
}
