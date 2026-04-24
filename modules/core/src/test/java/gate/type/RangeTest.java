package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class RangeTest
{

	@Test
	public void test01() throws ParseException
	{
		String expected = "1 - 2";
		assertEquals(expected, Range.of(expected).toString());
	}

	@Test
	public void test02()
	{
		int value = 0;
		for (@SuppressWarnings("unused")
		long i : Range.of(0, 10))
			value++;
		assertEquals(11, value);
	}

	@Test
	public void test03()
	{
		long value = Range.of(1, 3).stream().sum();

		assertEquals(6, value);
	}

	@Test
	public void test04() throws ParseException
	{
		long value = Range.of("1-3").stream().sum();

		assertEquals(6, value);
	}

	@Test
	public void test05() throws ParseException
	{
		String expected = "1 - 2";
		assertEquals(expected, Range.of("1-2").toString());
	}

	@Test
	public void test06() throws ParseException
	{
		String expected = "1";
		assertEquals(expected, Range.of(expected).toString());
	}

	@Test
	public void test07() throws ParseException
	{
		String expected = "1";
		assertEquals(expected, Range.of(1, 1).toString());
	}
}
