package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.template.Template;

import java.lang.reflect.Type;
import java.util.List;

public class TemplateConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints() {return List.of();}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

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
			return Template.compile(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(string + " is not a valid template", ex);
		}
	}

}