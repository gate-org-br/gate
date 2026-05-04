package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.br.CNPJ;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de CNPJ devem ser preenchidos no formato 99.999.999/9999-99")
public class CNPJConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(18));
		constraints.add(new Pattern.Implementation(CNPJ.FORMATTED.toString()));
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
			return CNPJ.valueOf(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(string + " não é um CNPJ válido.");
		}
	}

}