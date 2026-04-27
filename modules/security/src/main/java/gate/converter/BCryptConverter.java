package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.constraint.Pattern;
import gate.security.hash.BCrypt;

import java.util.LinkedList;
import java.util.List;

@Description("BCrypt Hash")
public class BCryptConverter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(60));
		constraints.add(new Pattern.Implementation("^\\$2[abyxy]?\\$[0-9]{2}\\$[A-Za-z0-9./]{53}$"));
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
	public Object ofString(Class<?> type, String string)
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				return BCrypt.of(string);
		}
		return null;
	}

}