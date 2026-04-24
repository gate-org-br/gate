package gate.policonverter;

import gate.type.collections.StringList;

public class StringListPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
	{
		return new StringList(value);
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		return ((StringList) value).toArray();
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		return new StringList(java.util.stream.Stream.of(objects).map(String::valueOf).toArray(String[]::new));
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
