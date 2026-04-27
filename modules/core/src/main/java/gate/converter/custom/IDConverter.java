package gate.converter.custom;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.ID;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de ID devem ser preenchidos com números inteiros positivos.")
public class IDConverter implements Converter
{

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;

			string = string.trim();
			if (string.isEmpty())
				return null;

			return ID.valueOf(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(ex, string + " não é um número válido");
		}
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(10));
		constraints.add(new Pattern.Implementation("^[0-9]{1,10}$"));
		return constraints;
	}

}
