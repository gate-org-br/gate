package gate.policonverter;

import gate.error.ConversionException;
import gate.type.collections.CharacterList;
import java.util.stream.Stream;
import javax.servlet.http.Part;

public class CharacterListPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
		throws ConversionException
	{
		return new CharacterList(Stream.of(value)
			.map(e -> e.charAt(0)).toArray(Character[]::new));
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public String[] getString(Class<?> type, Object value)
		throws ConversionException
	{
		return Stream.of(((CharacterList) value).toArray()).map(e -> String.valueOf(e))
			.toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	public static class CommaPoliconverter extends CharacterListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
			throws ConversionException
		{
			return new CharacterList.Comma(Stream.of(value).filter(e -> !e.isEmpty())
				.map(e -> e.charAt(0)).toArray(Character[]::new));
		}

		@Override
		public String[] getString(Class<?> type, Object value)
			throws ConversionException
		{
			return Stream.of(((CharacterList.Comma) value).toArray()).map(e -> String.valueOf(e))
				.toArray(String[]::new);
		}
	}

	public static class SemicolonPoliconverter extends CharacterListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
			throws ConversionException
		{
			return new CharacterList.Semicolon(Stream.of(value).filter(e -> !e.isEmpty())
				.map(e -> e.charAt(0)).toArray(Character[]::new));
		}

		@Override
		public String[] getString(Class<?> type, Object value)
			throws ConversionException
		{
			return Stream.of(((CharacterList.Semicolon) value).toArray()).map(e -> String.valueOf(e))
				.toArray(String[]::new);

		}
	}

	public static class LineBreakPoliconverter extends CharacterListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
			throws ConversionException
		{
			return new CharacterList.LineBreak(Stream.of(value).filter(e -> !e.isEmpty())
				.map(e -> e.charAt(0)).toArray(Character[]::new));
		}

		@Override
		public String[] getString(Class<?> type, Object value)
			throws ConversionException
		{
			return Stream.of(((CharacterList.LineBreak) value).toArray()).map(e -> String.valueOf(e))
				.toArray(String[]::new);
		}
	}
}
