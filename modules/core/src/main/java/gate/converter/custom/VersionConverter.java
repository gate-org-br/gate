package gate.converter.custom;
import gate.annotation.Description;

import gate.constraint.Constraint;
import gate.constraint.Pattern;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Version;
import java.util.Collections;
import java.util.List;

@Description("Número de versão padrão maven")
public class VersionConverter implements Converter
{

	private static final List<Constraint.Implementation<?>> CONSTRAINTS
		= Collections.singletonList(new Pattern.Implementation(Version.PATTERN.toString()));

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return CONSTRAINTS;
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
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				return Version.of(string);
		}
		return null;
	}

}
