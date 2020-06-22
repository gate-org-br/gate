package gate.policonverter;

import gate.converter.Converter;
import gate.error.AppError;
import gate.error.ConversionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.http.Part;

public class EnumSetPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
	{
		try
		{
			EnumSet<?> objects = (EnumSet<?>) EnumSet.class.getMethod("noneOf", Class.class).invoke(EnumSet.class, type);
			for (String string : value)
				EnumSet.class.getMethod("add", Object.class).invoke(objects, Converter.getConverter(type).ofString(type,
					string));
			return objects;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ConversionException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public Object getObject(Class<?> type, Part[] value)
	{
		try
		{
			EnumSet<?> objects = (EnumSet<?>) EnumSet.class.getMethod("noneOf", Class.class).invoke(EnumSet.class, type);
			for (Part part : value)
				EnumSet.class.getMethod("add", Object.class).invoke(objects, Converter.getConverter(type).ofPart(type,
					part));
			return objects;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ConversionException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		List<String> strings = new ArrayList<>();
		for (Enum<?> object : (EnumSet<?>) value)
			strings.add(Converter.getConverter(type).toString(type, object));
		return strings.toArray(new String[0]);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new UnsupportedOperationException("getAttachments");
	}
}
