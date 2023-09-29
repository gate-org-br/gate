package gate.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CPFTest
{

	@Test
	public void testValidate1()
	{
		assertTrue(CPF.validate("314.343.884-33"));
	}

	@Test
	public void testValidate2()
	{
		assertTrue(CPF.validate("977.234.477-79"));
	}

	@Test
	public void testValidate3()
	{
		assertFalse(CPF.validate("000.000.000-01"));
	}

	@Test
	public void testToFormat1()
	{
		assertNull(CPF.format("0"));
	}

	@Test
	public void testToFormat2()
	{
		assertEquals("000.000.000-00", CPF.format("00"));
	}

	@Test
	public void testToFormat3()
	{
		assertEquals("000.000.000-00", CPF.format("000"));
	}

	@Test
	public void testToFormat4()
	{
		assertEquals("000.000.000-00", CPF.format("0000"));
	}

	@Test
	public void testToFormat5()
	{
		assertEquals("000.000.000-00", CPF.format("00000"));
	}

	@Test
	public void testToFormat6()
	{
		assertEquals("000.000.000-00", CPF.format("000000"));
	}

	@Test
	public void testToFormat7()
	{
		assertEquals("000.000.000-00", CPF.format("0000000"));
	}

	@Test
	public void testToFormat8()
	{
		assertEquals("000.000.000-00", CPF.format("00000000"));
	}

	@Test
	public void testToFormat9()
	{
		assertEquals("000.000.000-00", CPF.format("000000000"));
	}

	@Test
	public void testToFormat10()
	{
		assertEquals("000.000.000-00", CPF.format("0000000000"));
	}

	@Test
	public void testToFormat11()
	{
		assertEquals("000.000.000-00", CPF.format("00000000000"));
	}

	@Test
	public void testToFormat14()
	{
		assertEquals("000.000.000-00", CPF.format("000.000.000-00"));
	}
}
