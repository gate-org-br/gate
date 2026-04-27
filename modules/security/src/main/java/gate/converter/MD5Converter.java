package gate.converter;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.constraint.Pattern;
import gate.security.hash.MD5;

import java.util.LinkedList;
import java.util.List;

@Description("MD5 Hash")
public class MD5Converter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(32));
		constraints.add(new Pattern.Implementation("^[A-Fa-f0-9]{32}$"));
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
				return MD5.of(string);
		}
		return null;
	}

}