package gate.policonverter;

import gate.type.collections.StringSet;

public class StringSetPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
	{
		return new StringSet(value);
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		return ((StringSet) value).toArray();
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		return new StringSet(java.util.stream.Stream.of(objects).map(String::valueOf).toArray(String[]::new));
	}

	public static class CommaPoliconverter extends StringSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new StringSet.Comma(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((StringSet.Comma) value).toArray();
		}
	}

	public static class SemicolonPoliconverter extends StringSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new StringSet.Semicolon(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((StringSet.Semicolon) value).toArray();
		}
	}

	public static class LineBreakPoliconverter extends StringSetPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new StringSet.LineBreak(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return ((StringSet.LineBreak) value).toArray();
		}
	}
}
