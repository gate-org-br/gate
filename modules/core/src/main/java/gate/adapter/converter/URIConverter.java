package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class URIConverter implements Converter
{
	private static final List<Constraint.Implementation<?>> CONSTRAINTS
			= Collections.singletonList(new Pattern.Implementation("^[a-zA-Z][a-zA-Z0-9+\\-.]*://[^\\s]+$"));

	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
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
			return new URI(string);
		} catch (URISyntaxException e)
		{
			throw new ConversionException(string + " is not a valid URI");
		}
	}

	@Override
	public String toString(Class<?> type, Object object) {return object != null ? object.toString() : "";}

	@Override
	public String toISOString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}
}