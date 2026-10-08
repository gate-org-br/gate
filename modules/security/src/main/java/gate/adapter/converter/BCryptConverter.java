package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.constraint.Pattern;
import gate.security.hash.BCrypt;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("BCrypt Hash")
public class BCryptConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(60));
		constraints.add(new Pattern.Implementation("^\\$2[abyxy]?\\$[0-9]{2}\\$[A-Za-z0-9./]{53}$"));
		return constraints;
	}

	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

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
	public Object ofString(Type type, String string)
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