package gate.policonverter;

import gate.error.ConversionException;
import gate.type.collections.StringSet;
import javax.servlet.http.Part;

public class StringSetPoliconverter extends Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
			throws ConversionException
	{
		return new StringSet(value);
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
		return ((StringSet) value).toArray();
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	public static class CommaPoliconverter extends StringSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
				throws ConversionException
		{
			return new StringSet.Comma(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
				throws ConversionException
		{
			return ((StringSet.Comma) value).toArray();
		}
	}

	public static class SemicolonPoliconverter extends StringSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
				throws ConversionException
		{
			return new StringSet.Semicolon(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
				throws ConversionException
		{
			return ((StringSet.Semicolon) value).toArray();
		}
	}

	public static class LineBreakPoliconverter extends StringSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
				throws ConversionException
		{
			return new StringSet.LineBreak(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
				throws ConversionException
		{
			return ((StringSet.LineBreak) value).toArray();
		}
	}
}
