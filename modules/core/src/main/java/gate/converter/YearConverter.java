package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;

public class YearConverter implements Converter
{

	@Override
	public String getMask()
	{
		return "####";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public String getDescription()
	{
		return "Campos de mês/ano devem ser preenchidos no formato YYYY";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? object.toString() : "";
	}

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
			return Year.parse(string);
		} catch (DateTimeParseException ex)
		{
			throw new ConversionException(ex, "%s não é um ano válido.", ex.getParsedString(), getDescription());
		}
	}

}
