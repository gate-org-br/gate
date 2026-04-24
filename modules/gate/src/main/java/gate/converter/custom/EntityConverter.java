package gate.converter.custom;

import gate.annotation.Entity;
import gate.converter.Converter;
import gate.converter.ObjectConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.lang.property.Property;

import java.lang.reflect.InvocationTargetException;

public class EntityConverter extends ObjectConverter
{

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string != null && !string.trim().isEmpty())
			{
				Property property = Property.getProperty(type, Entity.Extractor.extract(type));
				Object entity = type.getDeclaredConstructor().newInstance();
				property.setValue(entity, Converter
						.getConverter(property.getRawType())
						.ofString(property.getRawType(), string));
				return entity;
			}

			return null;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
		         | InvocationTargetException | RuntimeException e)
		{
			throw new ConversionException(String.format("%s não é uma entidade válida.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		try
		{
			if (object == null)
				return "";
			return Converter.toString(Property.getProperty(type, Entity.Extractor.extract(type))
					.getValue(object));
		} catch (RuntimeException e)
		{
			throw new AppError(e);
		}
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

}