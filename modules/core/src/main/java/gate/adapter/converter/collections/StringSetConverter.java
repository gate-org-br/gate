package gate.adapter.converter.collections;

import gate.annotation.Description;
import gate.constraint.Constraint;
import gate.adapter.converter.SetConverter;
import gate.type.collections.StringSet;

import java.util.Collections;
import java.util.List;

@Description("Lista separada por vírgulas")
public class StringSetConverter extends SetConverter
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
	public Object ofString(Class<?> type, String string)
	{
		return string != null ? new StringSet(string) : null;
	}

	public static class CommaConverter extends StringSetConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new StringSet.Comma(string) : null;
		}
	}

	public static class SemicolonConverter extends StringSetConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new StringSet.Semicolon(string) : null;
		}
	}

	public static class LineBreakConverter extends StringSetConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new StringSet.LineBreak(string) : null;
		}
	}
}