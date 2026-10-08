package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.EMail;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de EMAILS devem conter endereços de E-Mail válidos")
public class EMailConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Pattern.Implementation(EMail.REGEX));
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
		try
		{
			return string != null && !string.isBlank() ? EMail.valueOf(string) : null;
		} catch (IllegalArgumentException e)
		{
			throw new ConversionException(string.concat(" não é um e-mail válido."));
		}
	}

}