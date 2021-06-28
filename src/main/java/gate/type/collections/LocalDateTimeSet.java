package gate.type.collections;

import gate.converter.Converter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.converter.collections.LocalDateTimeSetConverter;
import gate.error.ConversionException;
import gate.policonverter.LocalDateTimeSetPoliconverter;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

@ElementType(LocalDateTime.class)
@Policonverter(LocalDateTimeSetPoliconverter.class)
@gate.annotation.Converter(LocalDateTimeSetConverter.class)
public class LocalDateTimeSet extends HashSet<LocalDateTime>
{

	private static final long serialVersionUID = 1L;

	public LocalDateTimeSet()
	{
	}

	public LocalDateTimeSet(String values) throws ConversionException
	{
		this(values.split(",|;|\\r?\\n"));
	}

	public LocalDateTimeSet(String... values) throws ConversionException
	{
		this(Arrays.asList(values));
	}

	public LocalDateTimeSet(List<String> values) throws ConversionException
	{
		for (String value : values)
		{
			value = value.trim();
			if (!value.isEmpty())
				add(Converter.fromString(LocalDateTime.class, value));
		}
	}

	@Override
	public String toString()
	{
		return stream().map(Converter::toString).collect(Collectors.joining(", "));
	}

	public String toString(String join)
	{
		return stream().map(Converter::toString).collect(Collectors.joining(join));
	}

	@Override
	public LocalDateTime[] toArray()
	{
		return toArray(new LocalDateTime[size()]);
	}

	@ElementType(LocalDateTime.class)
	@gate.annotation.Converter(LocalDateTimeSetConverter.CommaConverter.class)
	@Policonverter(LocalDateTimeSetPoliconverter.CommaPoliconverter.class)
	public static class Comma extends LocalDateTimeSet
	{

		private static final long serialVersionUID = 1L;

		public Comma()
		{
		}

		public Comma(String values) throws ConversionException
		{
			super(values.trim().split(","));
		}

		public Comma(String... values) throws ConversionException
		{
			super(values);
		}

		public Comma(List<String> values) throws ConversionException
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
	@gate.annotation.Converter(LocalDateTimeSetConverter.SemicolonConverter.class)
	@Policonverter(LocalDateTimeSetPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends LocalDateTimeSet
	{

		private static final long serialVersionUID = 1L;

		public Semicolon()
		{
		}

		public Semicolon(String values) throws ConversionException
		{
			super(values.trim().split(";"));
		}

		public Semicolon(String... values) throws ConversionException
		{
			super(values);
		}

		public Semicolon(List<String> values) throws ConversionException
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
	@gate.annotation.Converter(LocalDateTimeSetConverter.LineBreakConverter.class)
	@Policonverter(LocalDateTimeSetPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends LocalDateTimeSet
	{

		private static final long serialVersionUID = 1L;

		public LineBreak()
		{
		}

		public LineBreak(String values) throws ConversionException
		{
			super(values.split("\r?\r|\n"));
		}

		public LineBreak(String... values) throws ConversionException
		{
			super(values);
		}

		public LineBreak(List<String> values) throws ConversionException
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
