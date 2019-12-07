package gate.type.collections;

import gate.annotation.Converter;
import gate.converter.collections.StringListConverter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.policonverter.StringListPoliconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ElementType(String.class)
@Converter(StringListConverter.class)
@Policonverter(StringListPoliconverter.class)
public class StringList extends ArrayList<String>
{

	private static final long serialVersionUID = 1L;

	public StringList()
	{
	}

	public StringList(String values)
	{
		this(values.split(",|;|\\r?\\n"));
	}

	public StringList(String... values)
	{
		this(Arrays.asList(values));
	}

	public StringList(List<String> values)
	{
		values.stream().map(String::trim).filter(v -> !v.isEmpty()).forEach(this::add);
	}

	@Override
	public String toString()
	{
		return stream().collect(Collectors.joining(String.format("%n")));
	}

	public String toString(String join)
	{
		return String.join(join, this);
	}

	@Override
	public String[] toArray()
	{
		return toArray(new String[size()]);
	}

	public StringList append(String format, Object... args)
	{
		add(String.format(format, args));
		return this;
	}

	@ElementType(String.class)
	@Converter(StringListConverter.CommaConverter.class)
	@Policonverter(StringListPoliconverter.CommaPoliconverter.class)
	public static class Comma extends StringList
	{

		private static final long serialVersionUID = 1L;

		public Comma()
		{
		}

		public Comma(String values)
		{
			super(values.split(","));
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
			return String.join(", ", this);
		}

		@Override
		public StringList.Comma append(String format, Object... args)
		{
			return (StringList.Comma) super.append(format, args);
		}
	}

	@ElementType(String.class)
	@Converter(StringListConverter.SemicolonConverter.class)
	@Policonverter(StringListPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends StringList
	{

		private static final long serialVersionUID = 1L;

		public Semicolon()
		{
		}

		public Semicolon(String values)
		{
			super(values.split(";"));
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
			return String.join("; ", this);
		}

		@Override
		public StringList.Semicolon append(String format, Object... args)
		{
			return (StringList.Semicolon) super.append(format, args);
		}

	}

	@ElementType(String.class)
	@Converter(StringListConverter.LineBreakConverter.class)
	@Policonverter(StringListPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends StringList
	{

		private static final long serialVersionUID = 1L;

		public LineBreak()
		{
		}

		public LineBreak(String values)
		{
			super(values.split("\\r?\\n"));
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
			return stream().collect(Collectors.joining(String.format("%n")));
		}

		@Override
		public StringList.LineBreak append(String format, Object... args)
		{
			return (StringList.LineBreak) super.append(format, args);
		}
	}

	public static boolean isEmpty(String string)
	{
		return string == null || string.isEmpty();
	}
}
