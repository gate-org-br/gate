package gate.converter.collections;

import gate.constraint.Constraint;
import gate.converter.CollectionConverter;
import gate.type.collections.StringList;

import java.util.Collections;
import java.util.List;

public class StringListConverter extends CollectionConverter
{
	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return "Lista separada por vírgulas";
	}

	@Override
	public String getMask()
	{
		return null;
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
		return string != null ? new StringList(string) : null;
	}

	public static class CommaConverter extends StringListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new StringList.Comma(string) : null;
		}
	}

	public static class SemicolonConverter extends StringListConverter
	{
		@Override
		public String getDescription()
		{
			return "Lista separada por ponto e vírgula";
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new StringList.Semicolon(string) : null;
		}
	}

	public static class LineBreakConverter extends StringListConverter
	{
		@Override
		public String getDescription()
		{
			return "Lista separada por quebra de linha";
		}

		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new StringList.LineBreak(string) : null;
		}
	}
}
