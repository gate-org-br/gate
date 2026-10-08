package gate.adapter.converter;

import gate.annotation.Entity;
import gate.error.ConversionException;
import gate.lang.constructionStrategy.ConstructionStrategy;
import gate.lang.property.Property;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.Map;

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

		var rawType = Reflection.getRawType(type);
		var name = Entity.Extractor.extract(rawType);
		var property = Property.getProperty(rawType, name);
		var propertyType = property.getRawType();
		var converter = Converter.getConverter(propertyType);
		var value = converter.ofString(propertyType, string);
		return ConstructionStrategy.newInstance(rawType, Map.of(property.getLastAttribute(), value));
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		var name = Entity.Extractor.extract(type);
		var property = Property.getProperty(type, name);
		var value = property.getValue(object);
		return Converter.toString(value);
	}
}