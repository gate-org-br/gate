package gate.converter.collections;

import gate.constraint.Constraint;
import gate.converter.CollectionConverter;
import gate.type.collections.CharacterList;

import java.util.Collections;
import java.util.List;

public class CharacterListConverter extends CollectionConverter
{
	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getDescription()
	{
		return "Lista de caracteres separada por vírgulas";
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
		return string != null ? new CharacterList(string) : null;
	}

	public static class CommaConverter extends CharacterListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new CharacterList.Comma(string) : null;
		}
	}

	public static class SemicolonConverter extends CharacterListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new CharacterList.Semicolon(string) : null;
		}
	}

	public static class LineBreakConverter extends CharacterListConverter
	{
		@Override
		public Object ofString(Class<?> type, String string)
		{
			return string != null ? new CharacterList.LineBreak(string) : null;
		}
	}
}
