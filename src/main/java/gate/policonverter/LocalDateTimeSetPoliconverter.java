package gate.policonverter;

import gate.error.ConversionException;
import gate.type.collections.LocalDateTimeSet;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import javax.servlet.http.Part;

public class LocalDateTimeSetPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value) throws ConversionException
	{
		try
		{
			return new LocalDateTimeSet(value);
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
		return Stream.of(((LocalDateTimeSet) value)).map(LocalDateTimeSet::toString).toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	public static class CommaPoliconverter extends LocalDateTimeSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			try
			{
				return new LocalDateTimeSet.Comma(value);
			} catch (ParseException e)
			{
				throw new ConversionException(e.getMessage());
			}
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((LocalDateTimeSet.Comma) value).stream().map(LocalDateTime::toString).toArray(String[]::new);
		}
	}

	public static class SemicolonPoliconverter extends LocalDateTimeSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			try
			{
				return new LocalDateTimeSet.Semicolon(value);
			} catch (ParseException e)
			{
				throw new ConversionException(e.getMessage());
			}
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((LocalDateTimeSet.Semicolon) value).stream().map(LocalDateTime::toString).toArray(String[]::new);
		}
	}

	public static class LineBreakPoliconverter extends LocalDateTimeSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value) throws ConversionException
		{
			try
			{
				return new LocalDateTimeSet.LineBreak(value);
			} catch (ParseException e)
			{
				throw new ConversionException(e.getMessage());
			}

		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((LocalDateTimeSet.LineBreak) value).stream().map(LocalDateTime::toString).toArray(String[]::new);
		}
	}
}
