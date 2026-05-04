package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Maxlength;
import gate.constraint.Pattern;
import gate.error.ConversionException;
import gate.type.br.BrasilianDocument;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("CPF ou CNPJ")
public class BrasilianDocumentConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(18));
		constraints.add(new Pattern.Implementation("^([0-9]{11})|([0-9]{3}[.][0-9]{3}[.][0-9]{3}[-][0-9]{2})|([0-9]{14})|([0-9]{2}.[0-9]{3}.[0-9]{3}[/][0-9]{4}-[0-9]{2})$"));
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
		if (string == null)
			return null;

		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return BrasilianDocument.valueOf(string);
		} catch (IllegalArgumentException ex)
		{
			throw new ConversionException(string + " não é um documento válido.");
		}
	}

}