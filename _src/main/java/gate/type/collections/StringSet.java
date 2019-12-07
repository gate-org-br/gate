package gate.type.collections;

import gate.annotation.Converter;
import gate.converter.collections.StringSetConverter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.policonverter.StringListPoliconverter;
import gate.policonverter.StringSetPoliconverter;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@ElementType(String.class)
@Converter(StringSetConverter.class)
@Policonverter(StringSetPoliconverter.class)
public class StringSet extends HashSet<String>
{

	private static final long serialVersionUID = 1L;

	public StringSet()
	{
	}

	public StringSet(String values)
	{
		this(values.split(",|;|\\r?\\n"));
	}

	public StringSet(String... values)
	{
		this(Arrays.asList(values));
	}

	public StringSet(Collection<String> values)
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

	public StringSet append(String format, Object... args)
	{
		add(String.format(format, args));
		return this;
	}

	@ElementType(String.class)
	@Converter(StringSetConverter.CommaConverter.class)
	@Policonverter(StringListPoliconverter.CommaPoliconverter.class)
	public static class Comma extends StringSet
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

		public Comma(Set<String> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return String.join(", ", this);
		}

		@Override
		public StringSet.Comma append(String format, Object... args)
		{
			return (StringSet.Comma) super.append(format, args);
		}
	}

	@ElementType(String.class)
	@Converter(StringSetConverter.SemicolonConverter.class)
	@Policonverter(StringListPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends StringSet
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

		public Semicolon(Set<String> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return String.join("; ", this);
		}

		@Override
		public StringSet.Semicolon append(String format, Object... args)
		{
			return (StringSet.Semicolon) super.append(format, args);
		}

	}

	@ElementType(String.class)
	@Converter(StringSetConverter.LineBreakConverter.class)
	@Policonverter(StringListPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends StringSet
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

		public LineBreak(Set<String> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().collect(Collectors.joining(String.format("%n")));
		}

		@Override
		public StringSet.LineBreak append(String format, Object... args)
		{
			return (StringSet.LineBreak) super.append(format, args);
		}
	}

	public static boolean isEmpty(String string)
	{
		return string == null || string.isEmpty();
	}
}
