package gate.policonverter;

import gate.error.ConversionException;
import gate.type.collections.DateTimeSet;
import java.text.ParseException;
import java.util.stream.Stream;
import javax.servlet.http.Part;

public class DateTimeSetPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value) throws ConversionException
	{
		try
		{
			return new DateTimeSet(value);
		} catch (ParseException e)
		{
			throw new ConversionException(e.getMessage());
		}
	}

	@Override
	public Object getObject(Class<?> type, Part[] value) throws ConversionException
	{
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public String[] getString(Class<?> type, Object value) throws ConversionException
	{
		return Stream.of(((DateTimeSet) value)).map(e -> e.toString()).toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	public static class CommaPoliconverter extends DateTimeSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			try
			{
				return new DateTimeSet.Comma(value);
			} catch (ParseException e)
			{
				throw new ConversionException(e.getMessage());
			}
		}

		@Override
		public String[] getString(Class<?> type, Object value) throws ConversionException
		{
			return ((DateTimeSet.Comma) value).stream().map(e -> e.toString()).toArray(String[]::new);
		}
	}

	public static class SemicolonPoliconverter extends DateTimeSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			try
			{
				return new DateTimeSet.Semicolon(value);
			} catch (ParseException e)
			{
				throw new ConversionException(e.getMessage());
			}
		}

		@Override
		public String[] getString(Class<?> type, Object value)
				throws ConversionException
		{
			return ((DateTimeSet.Semicolon) value).stream().map(e -> e.toString()).toArray(String[]::new);
		}
	}

	public static class LineBreakPoliconverter extends DateTimeSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			try
			{
				return new DateTimeSet.LineBreak(value);
			} catch (ParseException e)
			{
				throw new ConversionException(e.getMessage());
			}

		}

		@Override
		public String[] getString(Class<?> type, Object value) throws ConversionException
		{
			return ((DateTimeSet.LineBreak) value).stream().map(e -> e.toString()).toArray(String[]::new);
		}
	}
}
