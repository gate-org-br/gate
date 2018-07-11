package gate.policonverter;

import gate.error.ConversionException;
import gate.type.collections.IntegerList;

import java.util.stream.Stream;
import javax.servlet.http.Part;

public class IntegerListPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value) throws ConversionException
	{
		return new IntegerList(value);
	}

	@Override
	public String[] getString(Class<?> type, Object value) throws ConversionException
	{
		return Stream.of(((IntegerList) value)).map(e -> e.toString()).toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		throw new java.lang.UnsupportedOperationException();
	}

	public static class CommaPoliconverter extends IntegerListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			return new IntegerList.Comma(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value) throws ConversionException
		{
			return Stream.of(((IntegerList.Comma) value)).map(e -> e.toString()).toArray(String[]::new);
		}
	}

	public static class SemicolonPoliconverter extends IntegerListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			return new IntegerList.Semicolon(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value) throws ConversionException
		{
			return Stream.of(((IntegerList.Semicolon) value)).map(e -> e.toString()).toArray(String[]::new);
		}
	}

	public static class LineBreakPoliconverter extends IntegerListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			return new IntegerList.LineBreak(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value) throws ConversionException
		{
			return Stream.of(((IntegerList.LineBreak) value)).map(e -> e.toString()).toArray(String[]::new);
		}
	}
}
