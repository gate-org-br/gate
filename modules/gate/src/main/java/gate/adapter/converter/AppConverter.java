package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.entity.App;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.util.List;

public class AppConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return List.of();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;
		return App.of(string);
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}