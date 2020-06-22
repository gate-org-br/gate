package gate.type;

import gate.error.AppException;
import gate.lang.property.Property;
import gate.converter.Converter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TextModel extends Model implements Serializable
{

	private static final long serialVersionUID = 1L;

	public TextModel(DataFile attachment)
	{
		super(attachment);
	}

	private Set<String> getPropertyNames(String string)
	{
		int indx1 = 0;
		Set<String> properties = new HashSet<>();
		do
		{
			indx1 = string.indexOf("${", indx1);
			if (indx1 == -1)
				break;
			int indx2 = string.indexOf("}", indx1);
			if (indx2 == -1)
				break;

			properties.add(string.substring(indx1 + 2, indx2));
			indx1 = indx2 + 1;
		} while (indx1 != -1);
		return properties;
	}

	@Override
	public DataFile newDocument(Object entity) throws AppException
	{
		try
		{
			String string = new String(getAttachment().getData());
			for (String name : getPropertyNames(string))
			{
				Property property = Property.getProperty(entity.getClass(), name);
				Class type = property.getRawType();
				String value = Converter
					.getConverter(type)
					.toString(type, property.getValue(entity));
				string = string.replace(String.format("${%s}", name), value);
			}
			return new DataFile(string.getBytes(), getAttachment().getName());
		} catch (IllegalArgumentException e)
		{
			throw new AppException(e.getMessage());
		}
	}
}
