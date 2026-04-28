package gate.adapter.converter.custom;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.constraint.Pattern;
import gate.adapter.converter.Converter;
import gate.type.SHA512;

import java.util.LinkedList;
import java.util.List;

@Description("SHA512 Hash")
public class SHA512Converter implements Converter
{

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(128));
		constraints.add(new Pattern.Implementation("^[A-Fa-f0-9]{128}$"));
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
				return SHA512.of(string);
		}
		return null;
	}

}