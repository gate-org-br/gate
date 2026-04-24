package gate.policonverter;

import gate.type.collections.IntegerList;
import java.util.stream.Stream;

public class IntegerListPoliconverter implements Policonverter
{

	@Override
	public Object getObject(Class<?> type, String[] value)
	{
		return new IntegerList(value);
	}

	@Override
	public String[] getString(Class<?> type, Object value)
	{
		return Stream.of(((IntegerList) value)).map(IntegerList::toString).toArray(String[]::new);
	}

	@Override
	public Object toCollection(Class<?> type, Object[] objects)
	{
		return new IntegerList(Stream.of(objects).map(Object::toString).toArray(String[]::new));
	}

	public static class CommaPoliconverter extends IntegerListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new IntegerList.Comma(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return Stream.of(((IntegerList.Comma) value)).map(IntegerList.Comma::toString).toArray(String[]::new);
		}
	}

	public static class SemicolonPoliconverter extends IntegerListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new IntegerList.Semicolon(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return Stream.of(((IntegerList.Semicolon) value)).map(IntegerList.Semicolon::toString).toArray(String[]::new);
		}
	}

	public static class LineBreakPoliconverter extends IntegerListPoliconverter
	{

		@Override
		public Object getObject(Class<?> type, String[] value)
		{
			return new IntegerList.LineBreak(value);
		}

		@Override
		public String[] getString(Class<?> type, Object value)
		{
			return Stream.of(((IntegerList.LineBreak) value)).map(IntegerList.LineBreak::toString).toArray(String[]::new);
		}
	}
}
