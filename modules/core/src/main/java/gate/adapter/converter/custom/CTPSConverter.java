package gate.adapter.converter.custom;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.adapter.converter.Converter;
import gate.error.ConversionException;
import gate.type.br.CTPS;

import java.util.LinkedList;
import java.util.List;

@Description("Campos de CTPS devem ser preenchidos no formato NNNNN SSSSS-UF")
public class CTPSConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(14));
		constraints.add(new Pattern.Implementation("^[0-9]{5} [0-9]{5}[-][A-Za-z]{2}$"));
		return constraints;
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
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
			{
				try
				{
					return new CTPS(string);
				} catch (Exception e)
				{
					throw new ConversionException(string.concat(" não é um CTPS válido."));
				}
			}
		}
		return null;
	}

}