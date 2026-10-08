package gate.adapter.converter;

import gate.adapter.metadata.Metadata;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.SIMCard;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de SIM Card devem ser possuir 20 dígitos")
public class SIMCardConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(20));
		constraints.add(new Pattern.Implementation("^[0-9]{20}$"));
		return constraints;
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				try
				{
					return SIMCard.valueOf(string);
				} catch (Exception e)
				{
					throw new ConversionException(Metadata.getMetadata(Reflection.getRawType(type)).description());
				}
		}
		return null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

}