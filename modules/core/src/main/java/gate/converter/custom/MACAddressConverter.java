package gate.converter.custom;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.type.MACAddress;

import java.util.LinkedList;
import java.util.List;

public class MACAddressConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(15));
		constraints.add(new Pattern.Implementation("^[0-9a-fA-F]{4}[.][0-9a-fA-F]{4}[.][0-9a-fA-F]{4}$"));
		return constraints;
	}

	@Override
	public String getMask()
	{
		return "****.****.****";
	}

	@Override
	public String getDescription()
	{
		return "Campos de endereço MAC devem ser preenchidos no formato HHHH.HHHH.HHHH";
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
					return new MACAddress(string);
				} catch (Exception e)
				{
					throw new ConversionException(string.concat(" não é um endereço MAC válido."));
				}
			}
		}
		return null;
	}

}
