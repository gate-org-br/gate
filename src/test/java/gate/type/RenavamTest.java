package gate.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author davins
 */
public class RenavamTest
{

	public RenavamTest()
	{
	}

	@Test
	public void testValidate0063988496_2()
	{
		assertTrue(Renavam.validate("0063988496-2"));
	}

	@Test
	public void testValidate00639884962()
	{
		assertTrue(Renavam.validate("00639884962"));
	}

	@Test
	public void testValidate63988496_2()
	{
		assertTrue(Renavam.validate("63988496-2"));
	}

	@Test
	public void testValidate639884962()
	{
		assertTrue(Renavam.validate("639884962"));
	}

	@Test
	public void testValidate1446676682_3()
	{
		assertTrue(Renavam.validate("1446676682-3"));
	}

	@Test
	public void testValidate14466766823()
	{
		assertTrue(Renavam.validate("14466766823"));
	}

	@Test
	public void testFormat0063988496_2()
	{
		assertEquals("0063988496-2", Renavam.format("0063988496-2"));
	}

	@Test
	public void testFormat00639884962()
	{
		assertEquals("0063988496-2", Renavam.format("00639884962"));
	}

	@Test
	public void testFormat63988496_2()
	{
		assertEquals("0063988496-2", Renavam.format("63988496-2"));
	}

	@Test
	public void testFormat639884962()
	{
		assertEquals("0063988496-2", Renavam.format("639884962"));
	}

	@Test
	public void testValidate1085897106()
	{
		assertTrue(Renavam.validate("1085897106"));
	}

	@Test
	public void testFormat1085897106()
	{
		assertEquals("0108589710-6", Renavam.format("1085897106"));
	}

	@Test
	public void testValidate10()
	{
		assertTrue(Renavam.validate("19"));
	}

	@Test
	public void testFormat19()
	{
		assertEquals("0000000001-9", Renavam.format("19"));
	}

	@Test
	public void testValidate0()
	{
		assertTrue(Renavam.validate("0"));
	}

	@Test
	public void testFormat0()
	{
		assertEquals("0000000000-0", Renavam.format("0"));
	}
}
