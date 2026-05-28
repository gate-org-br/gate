package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.constraint.Maxlength;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class CharacterConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		List<Constraint.Implementation<?>> constraints = new LinkedList<>();
		constraints.add(new Maxlength.Implementation(1));
		return constraints;

	}

	@Override
	public Object ofString(Type type, String string)
	{
		return string != null && !string.isEmpty() ? string.charAt(0) : null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

}