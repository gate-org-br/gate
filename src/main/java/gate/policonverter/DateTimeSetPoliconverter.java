package gate.policonverter;

import gate.error.ConversionException;
import gate.type.DateTime;
import gate.type.collections.DateTimeSet;
import java.text.ParseException;
import java.util.stream.Stream;
import javax.servlet.http.Part;

public class DateTimeSetPoliconverter implements Policonverter
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
	public Object getObject(Class<?> type, Part[] value)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		return Stream.of(((DateTimeSet) value)).map(DateTimeSet::toString).toArray(String[]::new);
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
		public String[] getString(Class<?> type, Object value)
		{
			return ((DateTimeSet.Comma) value).stream().map(DateTime::toString).toArray(String[]::new);
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
		{
			return ((DateTimeSet.Semicolon) value).stream().map(DateTime::toString).toArray(String[]::new);
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
		public String[] getString(Class<?> type, Object value)
		{
			return ((DateTimeSet.LineBreak) value).stream().map(DateTime::toString).toArray(String[]::new);
		}
	}
}
