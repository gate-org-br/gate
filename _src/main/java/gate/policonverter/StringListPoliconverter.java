package gate.policonverter;

import gate.type.collections.StringList;
import javax.servlet.http.Part;

public class StringListPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
	{
		return new StringList(value);
	}

	@Override
	public Object getObject(Class<?> type, Part[] value)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		return ((StringList) value).toArray();
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		throw new java.lang.UnsupportedOperationException();
	}

	public static class CommaPoliconverter extends StringListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new StringList.Comma(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((StringList.Comma) value).toArray();
		}
	}

	public static class SemicolonPoliconverter extends StringListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new StringList.Semicolon(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((StringList.Semicolon) value).toArray();
		}
	}

	public static class LineBreakPoliconverter extends StringListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new StringList.LineBreak(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((StringList.LineBreak) value).toArray();
		}
	}
}
