package gate.adapter.converter;

import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.error.ConversionException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Description("Expressão regular.")
public class PatternConverter implements Converter
{
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		try
		{
			return string != null && string.trim().length() > 0 ? Pattern.compile(string) : null;
		} catch (PatternSyntaxException e)
		{
			throw new ConversionException(String.format("%s não é uma expressão regular válida.", string));
		}
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? ((Pattern) object).pattern() : "";
	}

}