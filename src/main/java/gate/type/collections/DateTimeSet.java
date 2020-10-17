package gate.type.collections;

import gate.annotation.Converter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.converter.collections.DateTimeSetConverter;
import gate.policonverter.DateTimeSetPoliconverter;
import gate.type.DateTime;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@ElementType(DateTime.class)
@Converter(DateTimeSetConverter.class)
@Policonverter(DateTimeSetPoliconverter.class)
public class DateTimeSet extends HashSet<DateTime>
{

	private static final long serialVersionUID = 1L;

	public DateTimeSet()
	{
	}

	public DateTimeSet(String values) throws ParseException
	{
		this(values.split(",|;|\\r?\\n"));
	}

	public DateTimeSet(String... values) throws ParseException
	{
		this(Arrays.asList(values));
	}

	public DateTimeSet(List<String> values) throws ParseException
	{
		for (String value : values)
		{
			value = value.trim();
			if (!value.isEmpty())
				add(DateTime.of(value));
		}
	}

	@Override
	public String toString()
	{
		return stream().map(DateTime::toString).collect(Collectors.joining(", "));
	}

	public String toString(String join)
	{
		return stream().map(DateTime::toString).collect(Collectors.joining(join));
	}

	@Override
	public DateTime[] toArray()
	{
		return toArray(new DateTime[size()]);
	}

	@ElementType(DateTime.class)
	@Converter(DateTimeSetConverter.CommaConverter.class)
	@Policonverter(DateTimeSetPoliconverter.CommaPoliconverter.class)
	public static class Comma extends DateTimeSet
	{

		private static final long serialVersionUID = 1L;

		public Comma()
		{
		}

		public Comma(String values) throws ParseException
		{
			super(values.trim().split(","));
		}

		public Comma(String... values) throws ParseException
		{
			super(values);
		}

		public Comma(List<String> values) throws ParseException
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(DateTime::toString).collect(Collectors.joining(", "));
		}
	}

	@ElementType(DateTime.class)
	@Converter(DateTimeSetConverter.SemicolonConverter.class)
	@Policonverter(DateTimeSetPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends DateTimeSet
	{

		private static final long serialVersionUID = 1L;

		public Semicolon()
		{
		}

		public Semicolon(String values) throws ParseException
		{
			super(values.trim().split(";"));
		}

		public Semicolon(String... values) throws ParseException
		{
			super(values);
		}

		public Semicolon(List<String> values) throws ParseException
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(DateTime::toString).collect(Collectors.joining("; "));
		}
	}

	@ElementType(DateTime.class)
	@Converter(DateTimeSetConverter.LineBreakConverter.class)
	@Policonverter(DateTimeSetPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends DateTimeSet
	{

		private static final long serialVersionUID = 1L;

		public LineBreak()
		{
		}

		public LineBreak(String values) throws ParseException
		{
			super(values.split("\r?\r|\n"));
		}

		public LineBreak(String... values) throws ParseException
		{
			super(values);
		}

		public LineBreak(List<String> values) throws ParseException
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(DateTime::toString).collect(Collectors.joining(String.format("%n ")));
		}
	}
}
