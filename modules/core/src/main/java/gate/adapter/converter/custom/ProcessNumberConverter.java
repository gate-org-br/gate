package gate.adapter.converter.custom;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.adapter.converter.Converter;
import gate.error.ConversionException;
import gate.type.br.ProcessNumber;

import java.util.LinkedList;
import java.util.List;

@Description("Campos de número de processo devem estar nos formatos 9999.99.99.999999-9 ou 99.99.99999-9 ou 9999999-99.9999.9.99.9999. Zeros à esquerda são obrigatórios. Caracteres de formatação são opcionais.")
public class ProcessNumberConverter implements Converter
{

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		try
		{
			return string != null && string.trim().length() > 0 ? new ProcessNumber(string) : null;
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um nº de processo válido."));
		}
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
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(25));
		constraints.add(new Pattern.Implementation(
				"^[0-9]{4}[.][0-9]{2}[.][0-9]{2}[.][0-9]{6}[-][0-9]|[0-9]{2}[.][0-9]{2}.[0-9]{5}[-][0-9]|[0-9]{7}-[0-9]{2}[.][0-9]{4}.[0-9].[0-9]{2}.[0-9]{4}|[0-9]{10}|[0-9]{15}|[0-9]{20}$"));
		return constraints;
	}

}