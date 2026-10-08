package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Description("Entre com um número de no máximo duas casas decimais seguido da unidade correspondente (B, K, M, G, T, P, E, Z ou Y)")
public class DataConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new ArrayList<>();
		constraints.add(new Pattern.Implementation("^\\s*[0-9]+([,][0-9]{1,2})?\\s*[BbKkMmGgTtPpEeZzYy]\\s*$"));
		return constraints;
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			if (string == null)
				return null;
			string = string.trim();
			if (string.isEmpty())
				return null;
			return Data.valueOf(string);
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(String.format("%s não representa uma quantidade de dados válida.", string));
		}

	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}