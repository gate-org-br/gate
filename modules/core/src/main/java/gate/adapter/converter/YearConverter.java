package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;

@Description("Campos de mês/ano devem ser preenchidos no formato YYYY")
public class YearConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

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
			return Year.parse(string);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex, "%s não é um ano válido.", ex.getParsedString(), Metadata.getMetadata(Reflection.getRawType(type)).description());
		}
	}

}
