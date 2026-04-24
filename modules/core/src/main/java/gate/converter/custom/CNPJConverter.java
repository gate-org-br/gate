package gate.converter.custom;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.br.CNPJ;

import java.util.LinkedList;
import java.util.List;

public class CNPJConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(18));
		constraints.add(new Pattern.Implementation(CNPJ.FORMATTED.toString()));
		return constraints;
	}

	@Override
	public String getMask()
	{
		return "##.###.###/####-##";
	}

	@Override
	public String getDescription()
	{
		return "Campos de CNPJ devem ser preenchidos no formato 99.999.999/9999-99";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
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
			return CNPJ.of(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(string + " não é um CNPJ válido.");
		}
	}

}