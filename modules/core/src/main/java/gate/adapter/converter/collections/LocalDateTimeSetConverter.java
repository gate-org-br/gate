package gate.adapter.converter.collections;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.adapter.converter.SetConverter;
import gate.error.ConversionException;
import gate.type.collections.LocalDateTimeSet;

import java.util.Collections;
import java.util.List;

@Description("Lista de datas e horas separada por vírgulas")
public class LocalDateTimeSetConverter extends SetConverter
{
	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, object.toString()) : "";
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		return string != null ? new LocalDateTimeSet(string) : null;
	}

	public static class CommaConverter extends LocalDateTimeSetConverter
	{
		@Override
		public Object ofString(Class<?> type, String string) throws ConversionException
		{
			return string != null ? new LocalDateTimeSet.Comma(string) : null;
		}
	}

	public static class SemicolonConverter extends LocalDateTimeSetConverter
	{
		@Override
		public Object ofString(Class<?> type, String string) throws ConversionException
		{
			return string != null ? new LocalDateTimeSet.Semicolon(string) : null;
		}
	}

	public static class LineBreakConverter extends LocalDateTimeSetConverter
	{
		@Override
		public Object ofString(Class<?> type, String string) throws ConversionException
		{
			return string != null ? new LocalDateTimeSet.LineBreak(string) : null;
		}
	}
}