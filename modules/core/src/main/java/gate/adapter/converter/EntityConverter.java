package gate.adapter.converter;

import gate.annotation.Entity;
import gate.error.ConversionException;
import gate.lang.property.Property;
import gate.util.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class EntityConverter extends ObjectConverter
{
	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			var rawType = Reflection.getRawType(type);
			Property property = Property.getProperty(rawType, Entity
					.Extractor.extract(rawType));
			Object entity = rawType.getDeclaredConstructor().newInstance();
			property.setConvertedValue(entity, string);
			return entity;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
		         | InvocationTargetException | RuntimeException e)
		{
			throw new ConversionException(String.format("%s não é uma entidade válida.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		return Converter.toString(Property.getProperty(type, Entity.Extractor.extract(type))
				.getValue(object));
	}
}