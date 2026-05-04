package gate.adapter.converter;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.constraint.Length;
import gate.constraint.Pattern;
import gate.type.SHA512;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Description("SHA512 Hash")
public class SHA512Converter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Length.Implementation(128));
		constraints.add(new Pattern.Implementation("^[A-Fa-f0-9]{128}$"));
		return constraints;
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
				return SHA512.valueOf(string);
		}
		return null;
	}

}