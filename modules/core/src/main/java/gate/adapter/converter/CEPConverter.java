package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.br.CEP;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("Campos de CEP devem ser preenchidos no formato 99999-999")
public class CEPConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(10));
		constraints.add(new Pattern.Implementation("^[0-9]{2}[.][0-9]{3}[-][0-9]{3}$"));
		return constraints;
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
					return CEP.valueOf(string);
				} catch (Exception e)
				{
					throw new ConversionException(string.concat(" não é um CEP válido."));
				}
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