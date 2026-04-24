package gate.converter.collections;

import gate.constraint.Constraint;
import gate.converter.CollectionConverter;
import gate.type.collections.IntegerList;

import java.util.Collections;
import java.util.List;

public class IntegerListConverter extends CollectionConverter
{
	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return "Lista de inteiros separada por vírgulas";
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
		return string != null ? new IntegerList(string) : null;
	}

	public static class CommaConverter extends IntegerListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new IntegerList.Comma(string) : null;
		}
	}

	public static class SemicolonConverter extends IntegerListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new IntegerList.Semicolon(string) : null;
		}
	}

	public static class LineBreakConverter extends IntegerListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new IntegerList.LineBreak(string) : null;
		}
	}
}
