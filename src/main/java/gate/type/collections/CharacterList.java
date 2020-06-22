package gate.type.collections;

import gate.annotation.Converter;
import gate.annotation.ElementType;
import gate.annotation.Policonverter;
import gate.converter.collections.CharacterListConverter;
import gate.policonverter.CharacterListPoliconverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ElementType(String.class)
@Converter(CharacterListConverter.class)
@Policonverter(CharacterListPoliconverter.class)
public class CharacterList extends ArrayList<Character>
{

	private static final long serialVersionUID = 1L;

	public CharacterList()
	{
	}

	public CharacterList(String values)
	{
		Arrays.stream(values.split(",")).map(String::trim).filter(e -> !e.isEmpty()).map(e -> e.charAt(0)).forEach(this::add);
	}

	public CharacterList(Character... values)
	{
		this(Arrays.asList(values));
	}

	public CharacterList(List<Character> values)
	{
		super(values);
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
	public Character[] toArray()
	{
		return toArray(new Character[size()]);
	}

	@ElementType(Character.class)
	@Converter(CharacterListConverter.CommaConverter.class)
	@Policonverter(CharacterListPoliconverter.CommaPoliconverter.class)
	public static class Comma extends CharacterList
	{

		private static final long serialVersionUID = 1L;

		public Comma()
		{
		}

		public Comma(String values)
		{
			Arrays.stream(values.split(",")).map(String::trim).filter(e -> !e.isEmpty()).map(e -> e.charAt(0)).forEach(this::add);
		}

		public Comma(Character... values)
		{
			super(values);
		}

		public Comma(List<Character> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(Object::toString).collect(Collectors.joining(", "));
		}
	}

	@ElementType(Character.class)
	@Converter(CharacterListConverter.SemicolonConverter.class)
	@Policonverter(CharacterListPoliconverter.SemicolonPoliconverter.class)
	public static class Semicolon extends CharacterList
	{

		private static final long serialVersionUID = 1L;

		public Semicolon()
		{
		}

		public Semicolon(String values)
		{
			Arrays.stream(values.split(";")).map(String::trim).filter(e -> !e.isEmpty()).map(e -> e.charAt(0)).forEach(this::add);
		}

		public Semicolon(Character... values)
		{
			super(values);
		}

		public Semicolon(List<Character> values)
		{
			super(values);
		}

		@Override
		public String toString()
		{
			return stream().map(Object::toString).collect(Collectors.joining("; "));
		}
	}

	@ElementType(Character.class)
	@Converter(CharacterListConverter.LineBreakConverter.class)
	@Policonverter(CharacterListPoliconverter.LineBreakPoliconverter.class)
	public static class LineBreak extends CharacterList
	{

		private static final long serialVersionUID = 1L;

		public LineBreak()
		{
		}

		public LineBreak(String values)
		{
			Arrays.stream(values.split("\r?\r|\n")).map(String::trim).filter(e -> !e.isEmpty()).map(e -> e.charAt(0)).forEach(this::add);
		}

		public LineBreak(Character... values)
		{
			super(values);
		}

		public LineBreak(List<Character> values)
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
