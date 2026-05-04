package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.br.ProcessNumber;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;


public class ProcessNumberConverter implements Converter
{

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			return string != null && !string.trim().isEmpty() ? new ProcessNumber(string) : null;
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um nº de processo válido."));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(25));
		constraints.add(new Pattern.Implementation("^[0-9]{4}[.][0-9]{2}[.][0-9]{2}[.][0-9]{6}[-][0-9]|[0-9]{2}[.][0-9]{2}.[0-9]{5}[-][0-9]|[0-9]{7}-[0-9]{2}[.][0-9]{4}.[0-9].[0-9]{2}.[0-9]{4}|[0-9]{10}|[0-9]{15}|[0-9]{20}$"));
		return constraints;
	}

}