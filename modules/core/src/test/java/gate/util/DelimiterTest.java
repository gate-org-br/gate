package gate.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DelimiterTest
{

	@Test
	public void testInsert()
	{
		assertEquals("\"value\"", Delimiter.DOUBLE.insert("value"));
	}

	@Test
	public void testInsertWithNull()
	{
		assertNull(Delimiter.DOUBLE.insert(null));
	}

	@Test
	public void testUnquote()
	{
		assertEquals("value", Delimiter.DOUBLE.unquote("\"value\""));
	}

	@Test
	public void testUnquoteWithUnquotedValue()
	{
		assertEquals("value", Delimiter.DOUBLE.unquote("value"));
	}

	@Test
	public void testUnquoteWithNull()
	{
		assertNull(Delimiter.DOUBLE.unquote(null));
	}

	@Test
	public void testUnquoteDefaultWithDoubleQuotes()
	{
		assertEquals("value", Delimiter.unquote("\"value\""));
	}

	@Test
	public void testUnquoteDefaultWithSingleQuotes()
	{
		assertEquals("value", Delimiter.unquote("'value'"));
	}

	@Test
	public void testUnquoteDefaultWithBackticks()
	{
		assertEquals("value", Delimiter.unquote("`value`"));
	}

	@Test
	public void testUnquoteDefaultWithBrackets()
	{
		assertEquals("[value]", Delimiter.unquote("[value]"));
	}

	@Test
	public void testUnquoteDefaultWithParenthesis()
	{
		assertEquals("(value)", Delimiter.unquote("(value)"));
	}

	@Test
	public void testUnquoteDefaultWithBraces()
	{
		assertEquals("{value}", Delimiter.unquote("{value}"));
	}

	@Test
	public void testUnquoteDefaultWithUnquotedValue()
	{
		assertEquals("value", Delimiter.unquote("value"));
	}

	@Test
	public void testUnquoteDefaultWithMismatchedQuotes()
	{
		assertEquals("\"value'", Delimiter.unquote("\"value'"));
	}

	@Test
	public void testUnquoteDefaultWithNull()
	{
		assertNull(Delimiter.unquote(null));
	}

	@Test
	public void testNone()
	{
		assertEquals("value", Delimiter.NONE.insert("value"));
		assertEquals("value", Delimiter.NONE.remove("value"));
	}

	@Test
	public void testBracket()
	{
		assertEquals("[value]", Delimiter.BRACKET.insert("value"));
		assertEquals("value", Delimiter.BRACKET.remove("[value]"));
	}

	@Test
	public void testParenthesis()
	{
		assertEquals("(value)", Delimiter.PARENTHESIS.insert("value"));
		assertEquals("value", Delimiter.PARENTHESIS.remove("(value)"));
	}

	@Test
	public void testBraces()
	{
		assertEquals("{value}", Delimiter.BRACES.insert("value"));
		assertEquals("value", Delimiter.BRACES.remove("{value}"));
	}

	@Test
	public void testConstructorWithNullDelimiter()
	{
		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> new Delimiter(null));

		assertEquals("start can't be null", exception.getMessage());
	}

	@Test
	public void testConstructorWithNullStart()
	{
		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> new Delimiter(null, "]"));

		assertEquals("start can't be null", exception.getMessage());
	}

	@Test
	public void testConstructorWithNullEnd()
	{
		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> new Delimiter("[", null));

		assertEquals("end can't be null", exception.getMessage());
	}
}