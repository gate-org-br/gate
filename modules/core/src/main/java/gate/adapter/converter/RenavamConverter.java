package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.br.Renavam;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de Renavam devem ser preenchidos no formato 9999999999-9")
public class RenavamConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(12));
		constraints.add(new Pattern.Implementation("^[0-9]{10}[-][0-9]$"));
		return constraints;
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
			return Renavam.valueOf(string);
		} catch (Exception e)
		{
			throw new ConversionException(string.concat(" não é um Renavam válido."));
		}
	}

}