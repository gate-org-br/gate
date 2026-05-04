package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.br.CPF;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de CPF devem ser preenchidos no formato 999.999.999-99")
public class CPFConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(14));
		constraints.add(new Pattern.Implementation(CPF.FORMATTED.toString()));
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
			return CPF.valueOf(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(string.concat(" não é um CPF válido."));
		}
	}

}