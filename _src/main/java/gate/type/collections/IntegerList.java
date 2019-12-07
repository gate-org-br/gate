package gate.type.collections;

import gate.annotation.Converter;
import gate.converter.collections.IntegerListConverter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.policonverter.IntegerListPoliconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ElementType(Integer.class)
@Converter(IntegerListConverter.class)
@Policonverter(IntegerListPoliconverter.class)
public class IntegerList extends ArrayList<Integer>
{

	private static final long serialVersionUID = 1L;

	public IntegerList()
	{
	}

	public IntegerList(String values)
	{
		this(values.split(",|;|\\r?\\n"));
	}

	public IntegerList(String... values)
	{
		this(Arrays.asList(values));
	}

	public IntegerList(List<String> values)
	{
		values.stream().map(String::trim).filter(v -> !v.isEmpty()).map(Integer::valueOf).forEach(this::add);
	}

	@Override
	public String toString()
	{
		return stream().map(Object::toString).collect(Collectors.joining(", "));
	}

	public String toString(String join)
	{
		return stream().map(Object::toString).collect(Collectors.joining(join));
	}

	@Override
	public Integer[] toArray()
	{
		return toArray(new Integer[size()]);
	}

	@ElementType(Integer.class)
	@Converter(IntegerListConverter.CommaConverter.class)
	@Policonverter(IntegerListPoliconverter.CommaPoliconverter.class)
	public static class Comma extends IntegerList
	{

		private static final long serialVersionUID = 1L;

		public Comma()
		{
		}

		public Comma(String values)
		{
			super(values.trim().split(","));
		}

		public Comma(String... values)
		{
			super(values);
		}

		public Comma(List<String> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(Object::toString).collect(Collectors.joining(", "));
		}
	}

	@ElementType(Integer.class)
	@Converter(IntegerListConverter.SemicolonConverter.class)
	@Policonverter(IntegerListPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends IntegerList
	{

		private static final long serialVersionUID = 1L;

		public Semicolon()
		{
		}

		public Semicolon(String values)
		{
			super(values.trim().split(";"));
		}

		public Semicolon(String... values)
		{
			super(values);
		}

		public Semicolon(List<String> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(Object::toString).collect(Collectors.joining("; "));
		}
	}

	@ElementType(Integer.class)
	@Converter(IntegerListConverter.LineBreakConverter.class)
	@Policonverter(IntegerListPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends IntegerList
	{

		private static final long serialVersionUID = 1L;

		public LineBreak()
		{
		}

		public LineBreak(String values)
		{
			super(values.split("\r?\r|\n"));
		}

		public LineBreak(String... values)
		{
			super(values);
		}

		public LineBreak(List<String> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(Object::toString).collect(Collectors.joining(String.format("%n ")));
		}
	}
}
