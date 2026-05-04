package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.MACAddress;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de endereço MAC devem ser preenchidos no formato HHHH.HHHH.HHHH")
public class MACAddressConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(15));
		constraints.add(new Pattern.Implementation("^[0-9a-fA-F]{4}[.][0-9a-fA-F]{4}[.][0-9a-fA-F]{4}$"));
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
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
			{
				try
				{
					return MACAddress.valueOf(string);
				} catch (Exception e)
				{
					throw new ConversionException(string.concat(" não é um endereço MAC válido."));
				}
			}
		}
		return null;
	}

}