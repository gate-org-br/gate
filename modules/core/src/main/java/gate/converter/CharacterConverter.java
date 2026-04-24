package gate.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;

import java.util.LinkedList;
import java.util.List;

public class CharacterConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(1));
		return constraints;

	}

	@Override
	public String getDescription()
	{
		return "Campos de CARACTERE devem ser preenchidos com um único caractere.";
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public Object ofString(Class<?> type, String string)
	{
		return string != null && !string.isEmpty() ? string.charAt(0) : null;
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

}
