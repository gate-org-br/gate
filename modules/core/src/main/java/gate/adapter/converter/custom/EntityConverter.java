package gate.adapter.converter.custom;

import gate.adapter.converter.Converter;
import gate.adapter.converter.ObjectConverter;
import gate.annotation.Entity;
import gate.error.ConversionException;
import gate.lang.property.Property;

import java.lang.reflect.InvocationTargetException;

public class EntityConverter extends ObjectConverter
{
	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			Property property = Property.getProperty(type, Entity
					.Extractor.extract(type));
			Object entity = type.getDeclaredConstructor().newInstance();
			property.setValue(entity, Converter
					.getConverter(property.getRawType())
					.ofString(property.getRawType(), string));
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