package gate.type.collections;

import gate.annotation.Converter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.converter.collections.LocalDateTimeSetConverter;
import gate.policonverter.LocalDateTimeSetPoliconverter;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

@ElementType(LocalDateTime.class)
@Converter(LocalDateTimeSetConverter.class)
@Policonverter(LocalDateTimeSetPoliconverter.class)
public class LocalDateTimeSet extends HashSet<LocalDateTime>
{

	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public LocalDateTimeSet()
	{
	}

	public LocalDateTimeSet(String values) throws ParseException
	{
		this(values.split(",|;|\\r?\\n"));
	}

	public LocalDateTimeSet(String... values) throws ParseException
	{
		this(Arrays.asList(values));
	}

	public LocalDateTimeSet(List<String> values)
	{
		for (String value : values)
		{
			value = value.trim();
			if (!value.isEmpty())
				add(LocalDateTime.parse(value, FORMATTER));
		}
	}

	@Override
	public String toString()
	{
		return stream().map(LocalDateTime::toString).collect(Collectors.joining(", "));
	}

	public String toString(String join)
	{
		return stream().map(LocalDateTime::toString).collect(Collectors.joining(join));
	}

	@Override
	public LocalDateTime[] toArray()
	{
		return toArray(new LocalDateTime[size()]);
	}

	@ElementType(LocalDateTime.class)
	@Converter(LocalDateTimeSetConverter.CommaConverter.class)
	@Policonverter(LocalDateTimeSetPoliconverter.CommaPoliconverter.class)
	public static class Comma extends LocalDateTimeSet
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
			return stream().map(LocalDateTime::toString).collect(Collectors.joining(", "));
		}
	}

	@ElementType(LocalDateTime.class)
	@Converter(LocalDateTimeSetConverter.SemicolonConverter.class)
	@Policonverter(LocalDateTimeSetPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends LocalDateTimeSet
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
			return stream().map(LocalDateTime::toString).collect(Collectors.joining("; "));
		}
	}

	@ElementType(LocalDateTime.class)
	@Converter(LocalDateTimeSetConverter.LineBreakConverter.class)
	@Policonverter(LocalDateTimeSetPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends LocalDateTimeSet
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
			return stream().map(LocalDateTime::toString).collect(Collectors.joining(String.format("%n ")));
		}
	}
}
