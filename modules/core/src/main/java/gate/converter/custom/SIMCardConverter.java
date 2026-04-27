package gate.converter.custom;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.SIMCard;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de SIM Card devem ser possuir 20 dígitos")
public class SIMCardConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(20));
		constraints.add(new Pattern.Implementation("^[0-9]{20}$"));
		return constraints;
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				try
			{
				return new SIMCard(string);
			} catch (Exception e)
			{
				throw new ConversionException(gate.lang.property.metadata.Metadata.getMetadata(type).description());
			}
		}
		return null;
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object.toString()) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

}
