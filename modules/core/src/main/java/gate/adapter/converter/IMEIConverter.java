package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.type.IMEI;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de IMEI devem ser preenchidos no formato 99-999999-999999-9")
public class IMEIConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(18));
		constraints.add(new Pattern.Implementation("^[0-9]{2}[-][0-9]{6}[-][0-9]{6}[-][0-9]{1}$"));
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
					return IMEI.valueOf(string);
				} catch (Exception e)
				{
					throw new ConversionException(String.format("%s não é um IMEI válido.", string));
				}
			}
		}
		return null;
	}

}