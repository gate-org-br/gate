package gate.converter;

import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.util.Reflection;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class SetConverter extends CollectionConverter
{

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a set");

		Set set = new HashSet<>();
		Converter converter = Converter.getConverter(Reflection.getRawType(elementType));

		do
		{
			scanner.scan();
			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
				set.add(converter.ofJson(scanner, elementType,
					Reflection.getElementType(elementType)));
			else if (!set.isEmpty())
				throw new ConversionException(scanner.getCurrent() + " is not a set");
		} while (JsonToken.Type.COMMA == scanner.getCurrent().getType());

		if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_ARRAY)
			throw new ConversionException(scanner.getCurrent() + " is not a set");

		scanner.scan();
		return set;

	}

}
