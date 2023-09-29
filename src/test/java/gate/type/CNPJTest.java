package gate.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CNPJTest
{

	@Test
	public void testValidate1()
	{
		assertTrue(CNPJ.validate("69.362.335/0001-56"));
	}

	@Test
	public void testValidate2()
	{
		assertTrue(CNPJ.validate("44.733.243/0001-04"));
	}

	@Test
	public void testValidate3()
	{
		assertTrue(CNPJ.validate("00.000.000/0001-91"));
	}

	@Test
	public void testValidate4()
	{
		assertFalse(CNPJ.validate("44.733.243/0001-98"));
	}

	@Test
	public void testToFormat1()
	{
		assertNull(CNPJ.format("0"));
	}

	@Test
	public void testToFormat2()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("00"));
	}

	@Test
	public void testToFormat3()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("000"));
	}

	@Test
	public void testToFormat4()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("0000"));
	}

	@Test
	public void testToFormat5()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("00000"));
	}

	@Test
	public void testToFormat6()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("000000"));
	}

	@Test
	public void testToFormat7()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("0000000"));
	}

	@Test
	public void testToFormat8()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("00000000"));
	}

	@Test
	public void testToFormat9()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("000000000"));
	}

	@Test
	public void testToFormat10()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("0000000000"));
	}

	@Test
	public void testToFormat11()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("00000000000"));
	}

	@Test
	public void testToFormat12()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("000000000000"));
	}

	@Test
	public void testToFormat13()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("0000000000000"));
	}

	@Test
	public void testToFormat14()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("00000000000000"));
	}

	@Test
	public void testToFormat18()
	{
		assertEquals("00.000.000/0000-00", CNPJ.format("00.000.000/0000-00"));
	}
}
